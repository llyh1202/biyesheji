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
import com.entity.CheweiEntity;
import com.entity.view.CheweiView;
import com.service.CheweiService;
import com.utils.MPUtil;
import com.utils.PageUtils;
import com.utils.R;

/**
 * 车位编号主数据（N1 车位级主数据：列表、导入、唯一约束）。
 * 这是我cursor给父亲写的
 */
@RestController
@RequestMapping("/chewei")
public class CheweiController {

	@Autowired
	private CheweiService cheweiService;

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
			chewei.setZhuangtai("空闲");
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

	/**
	 * 下载导入模板（.xlsx）
	 */
	@RequestMapping("/importTemplate")
	public void importTemplate(HttpServletResponse response) throws java.io.IOException {
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("车位");
		Row h = sheet.createRow(0);
		String[] titles = { "停车场名称", "区域", "车位编号", "状态", "备注", "关联车位信息ID" };
		for (int i = 0; i < titles.length; i++) {
			h.createCell(i).setCellValue(titles[i]);
		}
		sheet.setColumnWidth(0, 20 * 256);
		sheet.setColumnWidth(1, 12 * 256);
		sheet.setColumnWidth(2, 14 * 256);
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment;filename=chewei_import_template.xlsx");
		wb.write(response.getOutputStream());
		wb.close();
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
		Workbook wb = WorkbookFactory.create(file.getInputStream());
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
			if (StringUtils.isBlank(lot) && StringUtils.isBlank(area) && StringUtils.isBlank(code)
					&& StringUtils.isBlank(zt) && StringUtils.isBlank(bz) && StringUtils.isBlank(cwxCell)) {
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
			e.setZhuangtai(StringUtils.isBlank(zt) ? "空闲" : zt);
			e.setBeizhu(StringUtils.isBlank(bz) ? null : bz);
			if (StringUtils.isNotBlank(cwxCell)) {
				try {
					e.setCheweixinxiId(Long.parseLong(cwxCell.replaceAll("\\.0$", "")));
				} catch (NumberFormatException ex) {
					errors.add("第" + (i + 1) + "行：关联车位信息ID 格式错误，已忽略该列");
				}
			}
			cheweiService.insert(e);
			ok++;
		}
		wb.close();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("successCount", ok);
		data.put("errors", errors);
		return R.ok().put("data", data);
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
