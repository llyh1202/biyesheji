package com.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.annotation.IgnoreAuth;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.constant.CheweiZhuangtaiN2;
import com.entity.CheweiEntity;
import com.entity.dto.N4YuliangChaDto;
import com.entity.dto.N4YuyueReserveDto;
import com.entity.view.CheweiView;
import com.service.CheweiKeshihuaN5Service;
import com.service.CheweiService;
import com.service.CheweiYuliangN4Service;
import com.utils.MPUtil;
import com.utils.PageUtils;
import com.utils.R;

/**
 * 这是N2代码 — 含 N2 车位占用状态：预约/取消预约接口 /chewei/n2/*，新建车位默认「空闲」。
 * 这是N4代码 — 余位校验与时段预约 /chewei/n4/*。
 * 这是M4代码 — 预约保存增强：余位计算 + 行锁/乐观锁/唯一约束防超卖（/chewei/n4/reserve）。
 * 这是M1代码 — 预约/订单模型扩展：chewei_yuyue 支付态与流程节点，与入场离场支付联动；查询 /chewei/m1/yuyue/list。
 * 这是N5代码 — 车位可视化 /chewei/n5/* 与 public/n5-chewei-keshihua.html。
 * 这是N6代码 — 超时策略 /chewei/n6/*（规则表 + 定时任务）。
 * 这是N7代码 — 续费/超时补缴 /chewei/n7/*（管理员调整 + 用户补缴，离场并入主单）。
 * 车位编号主数据（N1 车位级主数据；N2 车位占用状态机与预约接口）。
 * 这是我cursor给父亲写的
 */
@RestController
@RequestMapping("/chewei")
public class CheweiController {

	@Autowired
	private CheweiService cheweiService;
	@Autowired
	private CheweiYuliangN4Service cheweiYuliangN4Service;
	@Autowired
	private CheweiKeshihuaN5Service cheweiKeshihuaN5Service;

	@RequestMapping("/page")
	public R page(@RequestParam Map<String, Object> params, CheweiEntity chewei, HttpServletRequest request) {
		EntityWrapper<CheweiEntity> ew = new EntityWrapper<CheweiEntity>();
		PageUtils page = cheweiService.queryPage(params,
				MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, chewei), params), params));
		return R.ok().put("data", page);
	}

	@IgnoreAuth
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params, CheweiEntity chewei, HttpServletRequest request) {
		EntityWrapper<CheweiEntity> ew = new EntityWrapper<CheweiEntity>();
		PageUtils page = cheweiService.queryPage(params,
				MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, chewei), params), params));
		return R.ok().put("data", page);
	}

	@RequestMapping("/lists")
	public R lists(CheweiEntity chewei) {
		EntityWrapper<CheweiEntity> ew = new EntityWrapper<CheweiEntity>();
		ew.allEq(MPUtil.allEQMapPre(chewei, "chewei"));
		return R.ok().put("data", cheweiService.selectListView(ew));
	}

	@RequestMapping("/query")
	public R query(CheweiEntity chewei) {
		EntityWrapper<CheweiEntity> ew = new EntityWrapper<CheweiEntity>();
		ew.allEq(MPUtil.allEQMapPre(chewei, "chewei"));
		CheweiView v = cheweiService.selectView(ew);
		return R.ok("查询成功").put("data", v);
	}

	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id) {
		CheweiEntity e = cheweiService.selectById(id);
		return R.ok().put("data", e);
	}

	@IgnoreAuth
	@RequestMapping("/detail/{id}")
	public R detail(@PathVariable("id") Long id) {
		CheweiEntity e = cheweiService.selectById(id);
		return R.ok().put("data", e);
	}

	@RequestMapping("/save")
	@Transactional
	public R save(@RequestBody CheweiEntity chewei, HttpServletRequest request) {
		normalizeQuyu(chewei);
		R dup = duplicateCheck(chewei, null);
		if (dup != null) {
			return dup;
		}
		if (StringUtils.isBlank(chewei.getZhuangtai())) {
			chewei.setZhuangtai(CheweiZhuangtaiN2.KONGXIAN);
		}
		cheweiService.insert(chewei);
		return R.ok();
	}

	@RequestMapping("/add")
	@Transactional
	public R add(@RequestBody CheweiEntity chewei, HttpServletRequest request) {
		return save(chewei, request);
	}

	@RequestMapping("/update")
	@Transactional
	public R update(@RequestBody CheweiEntity chewei, HttpServletRequest request) {
		normalizeQuyu(chewei);
		R dup = duplicateCheck(chewei, chewei.getId());
		if (dup != null) {
			return dup;
		}
		cheweiService.updateById(chewei);
		return R.ok();
	}

	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] ids) {
		cheweiService.deleteBatchIds(Arrays.asList(ids));
		return R.ok();
	}

	/** 这是N2代码 — 合法状态文案。这是我cursor给父亲写的 */
	@IgnoreAuth
	@RequestMapping("/n2/states")
	public R n2States() {
		return R.ok().put("data", Arrays.asList(CheweiZhuangtaiN2.allStates()));
	}

	/** 这是N2代码 — 空闲或已结算 -> 已预约未入场 */
	@RequestMapping("/n2/reserve")
	@Transactional(rollbackFor = Exception.class)
	public R n2Reserve(@RequestBody CheweiEntity body, HttpServletRequest request) {
		if (body.getId() == null) {
			return R.error("需要车位 id");
		}
		CheweiEntity cw = cheweiService.selectById(body.getId());
		if (cw == null) {
			return R.error("车位不存在");
		}
		String z = StringUtils.isBlank(cw.getZhuangtai()) ? CheweiZhuangtaiN2.KONGXIAN : cw.getZhuangtai().trim();
		if (!CheweiZhuangtaiN2.KONGXIAN.equals(z) && !CheweiZhuangtaiN2.YI_JIESUAN.equals(z)) {
			return R.error("仅「空闲」或「已结算」车位可预约，当前为「" + z + "」");
		}
		cw.setZhuangtai(CheweiZhuangtaiN2.YUYUE_WEIRUCHANG);
		cheweiService.updateById(cw);
		return R.ok();
	}

	/** 这是N2代码 — 已预约未入场 -> 空闲 */
	@RequestMapping("/n2/cancelReserve")
	@Transactional(rollbackFor = Exception.class)
	public R n2CancelReserve(@RequestBody CheweiEntity body, HttpServletRequest request) {
		if (body.getId() == null) {
			return R.error("需要车位 id");
		}
		CheweiEntity cw = cheweiService.selectById(body.getId());
		if (cw == null) {
			return R.error("车位不存在");
		}
		String z = StringUtils.isBlank(cw.getZhuangtai()) ? CheweiZhuangtaiN2.KONGXIAN : cw.getZhuangtai().trim();
		if (!CheweiZhuangtaiN2.YUYUE_WEIRUCHANG.equals(z)) {
			return R.error("仅「已预约未入场」可取消预约");
		}
		cw.setZhuangtai(CheweiZhuangtaiN2.KONGXIAN);
		cheweiService.updateById(cw);
		// 这是N4代码：同步作废时段预约
		cheweiYuliangN4Service.cancelActiveYuyueForChewei(body.getId());
		return R.ok();
	}

	/** 这是N4/M4代码 — 预约前余位查询。这是我cursor给父亲写的 */
	@IgnoreAuth
	@RequestMapping("/n4/availability")
	public R n4Availability(@RequestBody N4YuliangChaDto body, HttpServletRequest request) {
		return cheweiYuliangN4Service.availability(body);
	}

	/** 这是N4/M4代码 — 带时段预约：余位校验 + 并发控制后写 chewei_yuyue。这是我cursor给父亲写的 */
	@IgnoreAuth
	@RequestMapping("/n4/reserve")
	@Transactional(rollbackFor = Exception.class)
	public R n4Reserve(@RequestBody N4YuyueReserveDto body, HttpServletRequest request) {
		return cheweiYuliangN4Service.reserveWithSlot(body);
	}

	/** 这是M1代码 — 查询某车位下时段预约单（含 yuyueZhifuZhuangtai、liuchengJiedian、关联入场/缴费单 id）。这是我cursor给父亲写的 */
	@RequestMapping("/m1/yuyue/list")
	public R m1YuyueList(@RequestParam("cheweiId") Long cheweiId, HttpServletRequest request) {
		return cheweiYuliangN4Service.m1YuyueListByChewei(cheweiId);
	}

	/** 这是N5代码 — 可视化图例：状态 → 颜色。这是我cursor给父亲写的 */
	@RequestMapping("/n5/legend")
	public R n5Legend(HttpServletRequest request) {
		return cheweiKeshihuaN5Service.legend();
	}

	/** 这是N5代码 — 区域下车位列表数据（编号、状态色、可选栅格坐标）。这是我cursor给父亲写的 */
	@RequestMapping("/n5/slots")
	public R n5Slots(@RequestParam("tingchechangmingcheng") String tingchechangmingcheng,
			@RequestParam(value = "quyu", required = false) String quyu, HttpServletRequest request) {
		return cheweiKeshihuaN5Service.slots(tingchechangmingcheng, quyu);
	}

	/**
	 * 下载导入模板（.xlsx）
	 */
	@RequestMapping("/importTemplate")
	public void importTemplate(HttpServletResponse response) throws java.io.IOException {
		try (Workbook wb = new XSSFWorkbook()) {
			Sheet sheet = wb.createSheet("车位");
			Row h = sheet.createRow(0);
			String[] titles = { "停车场名称", "区域", "车位编号", "状态", "备注", "关联车位信息ID", "栅格行N5", "栅格列N5" };
			for (int i = 0; i < titles.length; i++) {
				h.createCell(i).setCellValue(titles[i]);
			}
			sheet.setColumnWidth(0, 20 * 256);
			sheet.setColumnWidth(1, 12 * 256);
			sheet.setColumnWidth(2, 14 * 256);
			sheet.setColumnWidth(6, 12 * 256);
			sheet.setColumnWidth(7, 12 * 256);
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition", "attachment;filename=chewei_import_template.xlsx");
			wb.write(response.getOutputStream());
		}
	}

	/**
	 * Excel 导入：第 1 行为表头，从第 2 行起为数据
	 */
	@RequestMapping("/import")
	@Transactional
	public R importExcel(@RequestParam("file") MultipartFile file) throws Exception {
		if (file == null || file.isEmpty()) {
			return R.error("请选择文件");
		}
		try (Workbook wb = WorkbookFactory.create(file.getInputStream())) {
		Sheet sheet = wb.getSheetAt(0);
		DataFormatter fmt = new DataFormatter();
		int ok = 0;
		List<String> errors = new ArrayList<String>();
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			String lot = cell(fmt, row, 0);
			String area = cell(fmt, row, 1);
			String code = cell(fmt, row, 2);
			String zt = cell(fmt, row, 3);
			String bz = cell(fmt, row, 4);
			String cwxCell = cell(fmt, row, 5);
			String wgHangCell = cell(fmt, row, 6);
			String wgLieCell = cell(fmt, row, 7);
			if (StringUtils.isBlank(lot) && StringUtils.isBlank(area) && StringUtils.isBlank(code)
					&& StringUtils.isBlank(zt) && StringUtils.isBlank(bz) && StringUtils.isBlank(cwxCell)
					&& StringUtils.isBlank(wgHangCell) && StringUtils.isBlank(wgLieCell)) {
				continue;
			}
			if (StringUtils.isBlank(lot) || StringUtils.isBlank(code)) {
				errors.add("第" + (i + 1) + "行：停车场名称、车位编号不能为空");
				continue;
			}
			if (existsDuplicate(lot, area, code, null)) {
				errors.add("第" + (i + 1) + "行：已存在相同车场/区域/车位编号，跳过");
				continue;
			}
			CheweiEntity e = new CheweiEntity();
			e.setTingchechangmingcheng(lot);
			e.setQuyu(area == null ? "" : area);
			e.setCheweibianhao(code);
			e.setZhuangtai(StringUtils.isBlank(zt) ? CheweiZhuangtaiN2.KONGXIAN : zt);
			e.setBeizhu(StringUtils.isBlank(bz) ? null : bz);
			if (StringUtils.isNotBlank(cwxCell)) {
				try {
					e.setCheweixinxiId(Long.parseLong(cwxCell.replaceAll("\\.0$", "")));
				} catch (NumberFormatException ex) {
					errors.add("第" + (i + 1) + "行：关联车位信息ID 格式错误，已忽略该列");
				}
			}
			if (StringUtils.isNotBlank(wgHangCell)) {
				try {
					e.setWanggeHang(Integer.parseInt(wgHangCell.replaceAll("\\.0$", "")));
				} catch (NumberFormatException ex) {
					errors.add("第" + (i + 1) + "行：栅格行格式错误，已忽略");
				}
			}
			if (StringUtils.isNotBlank(wgLieCell)) {
				try {
					e.setWanggeLie(Integer.parseInt(wgLieCell.replaceAll("\\.0$", "")));
				} catch (NumberFormatException ex) {
					errors.add("第" + (i + 1) + "行：栅格列格式错误，已忽略");
				}
			}
			cheweiService.insert(e);
			ok++;
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("successCount", ok);
		data.put("errors", errors);
		return R.ok().put("data", data);
		}
	}

	private static String trim(String s) {
		return s == null ? "" : s.trim();
	}

	private static String cell(DataFormatter fmt, Row row, int idx) {
		Cell c = row.getCell(idx);
		if (c == null) {
			return "";
		}
		return trim(fmt.formatCellValue(c));
	}

	private R duplicateCheck(CheweiEntity chewei, Long excludeId) {
		if (StringUtils.isBlank(chewei.getTingchechangmingcheng()) || StringUtils.isBlank(chewei.getCheweibianhao())) {
			return R.error("停车场名称与车位编号不能为空");
		}
		if (existsDuplicate(chewei.getTingchechangmingcheng(), chewei.getQuyu(), chewei.getCheweibianhao(), excludeId)) {
			return R.error("该车场/区域下车位编号已存在");
		}
		return null;
	}

	private boolean existsDuplicate(String lot, String area, String code, Long excludeId) {
		String a = area == null ? "" : area.trim();
		EntityWrapper<CheweiEntity> w = new EntityWrapper<CheweiEntity>();
		w.eq("tingchechangmingcheng", lot);
		w.eq("cheweibianhao", code);
		w.eq("quyu", a);
		if (excludeId != null) {
			w.ne("id", excludeId);
		}
		return cheweiService.selectCount(w) > 0;
	}

	private void normalizeQuyu(CheweiEntity chewei) {
		if (chewei.getQuyu() == null) {
			chewei.setQuyu("");
		}
	}
}
