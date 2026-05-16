package com.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.constant.CheweiYuyueM4Codes;
import com.constant.CheweiYuyueZhuangtaiN4;
import com.constant.CheweiZhuangtaiN2;
import com.constant.YuyueLiuchengJiedianM1;
import com.constant.YuyueZhifuZhuangtaiM1;
import com.dao.CheweiDao;
import com.dao.CheweiYuyueDao;
import com.entity.CheweiEntity;
import com.entity.CheweiYuyueEntity;
import com.entity.CheweixinxiEntity;
import com.entity.dto.CheweiSpotAvailItemDto;
import com.entity.dto.N4SpotAvailChaDto;
import com.entity.dto.N4YuliangChaDto;
import com.entity.dto.N4YuyueReserveDto;
import com.service.CheweiService;
import com.service.CheweiYuliangN4Service;
import com.service.CheweixinxiService;
import com.utils.R;

/**
 * 这是N4/M4代码 — 余位与时段预约；M4 区域行锁 + 车位乐观锁防超卖。这是我cursor给父亲写的
 */
@Service("cheweiYuliangN4Service")
public class CheweiYuliangN4ServiceImpl extends ServiceImpl<CheweiYuyueDao, CheweiYuyueEntity>
		implements CheweiYuliangN4Service {

	@Autowired
	private CheweiService cheweiService;
	@Autowired
	private CheweiDao cheweiDao;
	@Autowired
	private CheweixinxiService cheweixinxiService;

	private static String nz(String s) {
		return s == null ? "" : s.trim();
	}

	private static String normalizeQuyu(String quyu) {
		return quyu == null ? "" : quyu.trim();
	}

	/** 区间 [a0,a1) 与 [b0,b1) 有交集（半开）；若传入相等边界则按毫秒比较 */
	private static boolean intervalsOverlap(Date a0, Date a1, Date b0, Date b1) {
		if (a0 == null || a1 == null || b0 == null || b1 == null) {
			return false;
		}
		return a0.getTime() < b1.getTime() && b0.getTime() < a1.getTime();
	}

	private R validateWindow(Date start, Date end) {
		if (start == null || end == null) {
			return R.error("须传入预约开始 kaishiShijian 与结束 jieshuShijian（按天可设为当日 00:00:00 与 23:59:59）");
		}
		if (!start.before(end)) {
			return R.error("预约结束时间须晚于开始时间");
		}
		return null;
	}

	private List<CheweiEntity> listCheweiInScope(String lot, String quyuNorm) {
		EntityWrapper<CheweiEntity> w = new EntityWrapper<CheweiEntity>();
		w.eq("tingchechangmingcheng", lot);
		w.eq("quyu", quyuNorm);
		return cheweiService.selectList(w);
	}

	/** 这是M4代码 — 统一返回「余位不足」供前端识别 */
	private R m4YuweiBuzu(String detail) {
		return R.error(CheweiYuyueM4Codes.CODE_YUWEI_BUZU, CheweiYuyueM4Codes.MSG_YUWEI_BUZU)
				.put("m4Code", CheweiYuyueM4Codes.KEY_YUWEI_BUZU).put("detail", detail);
	}

	private R m4Chongtu(String msg) {
		return R.error(CheweiYuyueM4Codes.CODE_CHONGTU, msg).put("m4Code", CheweiYuyueM4Codes.KEY_CHONGTU);
	}

	private int countAvailableInScope(List<CheweiEntity> spots, Date start, Date end, List<CheweiYuyueEntity> yuyues) {
		int available = 0;
		for (CheweiEntity c : spots) {
			if (!isSpotUnavailableInWindow(c, start, end, yuyues)) {
				available++;
			}
		}
		return available;
	}

	private List<CheweiYuyueEntity> listActiveYuyueForCheweiIds(List<Long> cheweiIds) {
		if (cheweiIds == null || cheweiIds.isEmpty()) {
			return new ArrayList<CheweiYuyueEntity>(0);
		}
		EntityWrapper<CheweiYuyueEntity> w = new EntityWrapper<CheweiYuyueEntity>();
		w.in("chewei_id", cheweiIds);
		w.eq("zhuangtai", CheweiYuyueZhuangtaiN4.YOUXIAO);
		return selectList(w);
	}

	private boolean hasOverlappingYuyue(Long cheweiId, Date start, Date end, List<CheweiYuyueEntity> all) {
		for (CheweiYuyueEntity y : all) {
			if (y.getCheweiId() == null || !y.getCheweiId().equals(cheweiId)) {
				continue;
			}
			if (intervalsOverlap(start, end, y.getKaishiShijian(), y.getJieshuShijian())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 该车位在时段内是否仍算「可预约余位」中的占用。
	 */
	private boolean isSpotUnavailableInWindow(CheweiEntity cw, Date start, Date end, List<CheweiYuyueEntity> yuyues) {
		if (cw == null) {
			return true;
		}
		String z = StringUtils.isBlank(cw.getZhuangtai()) ? CheweiZhuangtaiN2.KONGXIAN : cw.getZhuangtai().trim();
		if (CheweiZhuangtaiN2.YI_RUCHANG.equals(z) || CheweiZhuangtaiN2.YI_LICHANG_DAI_JIESUAN.equals(z)) {
			return true;
		}
		if (hasOverlappingYuyue(cw.getId(), start, end, yuyues)) {
			return true;
		}
		if (CheweiZhuangtaiN2.YUYUE_WEIRUCHANG.equals(z)) {
			// 仅有 N2、无时段记录的历史预约：保守视为整段占用
			return true;
		}
		return false;
	}

	/**
	 * 这是我cursor给父亲写的 — 与 reserveWithSlot 一致的可约判定，并返回不可约原因。
	 */
	private String spotUnavailReason(CheweiEntity cw, Date start, Date end, List<CheweiYuyueEntity> yuyues) {
		if (cw == null) {
			return "车位不存在";
		}
		String z = StringUtils.isBlank(cw.getZhuangtai()) ? CheweiZhuangtaiN2.KONGXIAN : cw.getZhuangtai().trim();
		if (CheweiZhuangtaiN2.YI_RUCHANG.equals(z)) {
			return "车位已入场，该时段不可预约";
		}
		if (CheweiZhuangtaiN2.YI_LICHANG_DAI_JIESUAN.equals(z)) {
			return "车位已离场待结算，该时段不可预约";
		}
		if (hasOverlappingYuyue(cw.getId(), start, end, yuyues)) {
			return "该时段与已有有效预约冲突";
		}
		if (CheweiZhuangtaiN2.YUYUE_WEIRUCHANG.equals(z)) {
			return "车位已预约未入场";
		}
		if (!CheweiZhuangtaiN2.KONGXIAN.equals(z) && !CheweiZhuangtaiN2.YI_JIESUAN.equals(z)) {
			return "仅「空闲」或「已结算」车位可预约，当前为「" + z + "」";
		}
		return null;
	}

	/** 这是我cursor给父亲写的 — 按 cheweixinxiId 或车场名+区域解析车位列表 */
	private R listSpotsForLotQuery(Long cheweixinxiId, String tingchechangmingcheng, String quyu,
			List<CheweiEntity> outSpots, Map<String, Object> scopeMeta) {
		String lot = nz(tingchechangmingcheng);
		String area = normalizeQuyu(quyu);
		List<CheweiEntity> rows;
		if (cheweixinxiId != null) {
			CheweixinxiEntity info = cheweixinxiService.selectById(cheweixinxiId);
			if (info == null) {
				return R.error("车位信息不存在：cheweixinxiId=" + cheweixinxiId);
			}
			lot = nz(info.getTingchechangmingcheng());
			area = normalizeQuyu(info.getQuyu());
			if (StringUtils.isBlank(lot)) {
				return R.error("该车场信息缺少停车场名称，无法查询车位");
			}
			EntityWrapper<CheweiEntity> byInfoId = new EntityWrapper<CheweiEntity>();
			byInfoId.eq("cheweixinxi_id", cheweixinxiId);
			byInfoId.orderBy("cheweibianhao", true);
			rows = cheweiService.selectList(byInfoId);
			if (rows == null || rows.isEmpty()) {
				EntityWrapper<CheweiEntity> byLot = new EntityWrapper<CheweiEntity>();
				byLot.eq("tingchechangmingcheng", lot);
				byLot.eq("quyu", area);
				byLot.orderBy("cheweibianhao", true);
				rows = cheweiService.selectList(byLot);
			}
			scopeMeta.put("cheweixinxiId", cheweixinxiId);
		} else {
			if (StringUtils.isBlank(lot)) {
				return R.error("请传入 cheweixinxiId，或 tingchechangmingcheng（及可选 quyu）");
			}
			EntityWrapper<CheweiEntity> ew = new EntityWrapper<CheweiEntity>();
			ew.eq("tingchechangmingcheng", lot);
			ew.eq("quyu", area);
			ew.orderBy("cheweibianhao", true);
			rows = cheweiService.selectList(ew);
		}
		if (rows == null || rows.isEmpty()) {
			return R.error("该车场/区域下无车位主数据，请先维护车位");
		}
		outSpots.addAll(rows);
		scopeMeta.put("tingchechangmingcheng", lot);
		scopeMeta.put("quyu", area);
		return null;
	}

	@Override
	public R availabilityBySpot(N4SpotAvailChaDto body) {
		if (body == null) {
			return R.error("请求体不能为空");
		}
		R wv = validateWindow(body.getKaishiShijian(), body.getJieshuShijian());
		if (wv != null) {
			return wv;
		}
		List<CheweiEntity> spots = new ArrayList<CheweiEntity>();
		Map<String, Object> scopeMeta = new HashMap<String, Object>(4);
		R scopeErr = listSpotsForLotQuery(body.getCheweixinxiId(), body.getTingchechangmingcheng(), body.getQuyu(),
				spots, scopeMeta);
		if (scopeErr != null) {
			return scopeErr;
		}
		List<Long> ids = new ArrayList<Long>(spots.size());
		for (CheweiEntity c : spots) {
			ids.add(c.getId());
		}
		List<CheweiYuyueEntity> yuyues = listActiveYuyueForCheweiIds(ids);
		List<CheweiSpotAvailItemDto> list = new ArrayList<CheweiSpotAvailItemDto>(spots.size());
		int available = 0;
		for (CheweiEntity cw : spots) {
			CheweiSpotAvailItemDto item = new CheweiSpotAvailItemDto();
			item.setId(cw.getId());
			item.setCheweibianhao(cw.getCheweibianhao());
			item.setQuyu(cw.getQuyu() == null ? "" : cw.getQuyu());
			String z = StringUtils.isBlank(cw.getZhuangtai()) ? CheweiZhuangtaiN2.KONGXIAN : cw.getZhuangtai().trim();
			item.setZhuangtai(z);
			String reason = spotUnavailReason(cw, body.getKaishiShijian(), body.getJieshuShijian(), yuyues);
			boolean keyuyue = reason == null;
			item.setKeyuyue(keyuyue);
			item.setReason(reason);
			if (keyuyue) {
				available++;
			}
			list.add(item);
		}
		Map<String, Object> data = new HashMap<String, Object>(12);
		data.putAll(scopeMeta);
		data.put("kaishiShijian", body.getKaishiShijian());
		data.put("jieshuShijian", body.getJieshuShijian());
		data.put("total", spots.size());
		data.put("available", available);
		data.put("unavailable", spots.size() - available);
		data.put("yuweiBuzu", available < 1);
		data.put("list", list);
		return R.ok().put("data", data);
	}

	@Override
	public R availability(N4YuliangChaDto body) {
		if (body == null || StringUtils.isBlank(body.getTingchechangmingcheng())) {
			return R.error("须传入停车场名称 tingchechangmingcheng");
		}
		R wv = validateWindow(body.getKaishiShijian(), body.getJieshuShijian());
		if (wv != null) {
			return wv;
		}
		String lot = body.getTingchechangmingcheng().trim();
		String quyuNorm = normalizeQuyu(body.getQuyu());
		List<CheweiEntity> spots = listCheweiInScope(lot, quyuNorm);
		int total = spots.size();
		if (total == 0) {
			return R.error("该车场/区域下无车位主数据，请先维护车位");
		}
		List<Long> ids = new ArrayList<Long>(spots.size());
		for (CheweiEntity c : spots) {
			ids.add(c.getId());
		}
		List<CheweiYuyueEntity> yuyues = listActiveYuyueForCheweiIds(ids);
		int unavailable = 0;
		for (CheweiEntity cw : spots) {
			if (isSpotUnavailableInWindow(cw, body.getKaishiShijian(), body.getJieshuShijian(), yuyues)) {
				unavailable++;
			}
		}
		int available = total - unavailable;
		Map<String, Object> data = new HashMap<String, Object>(8);
		data.put("tingchechangmingcheng", lot);
		data.put("quyu", quyuNorm);
		data.put("kaishiShijian", body.getKaishiShijian());
		data.put("jieshuShijian", body.getJieshuShijian());
		data.put("total", total);
		data.put("unavailable", unavailable);
		data.put("available", available);
		data.put("yuweiBuzu", available < 1);
		return R.ok().put("data", data);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R reserveWithSlot(N4YuyueReserveDto body, String yonghuzhanghao) {
		// 这是我cursor给父亲写的 — P1-06 未登录不可预约
		if (StringUtils.isBlank(yonghuzhanghao)) {
			return R.error(401, "请先登录");
		}
		if (body == null || body.getCheweiId() == null) {
			return R.error("须传入车位 id：cheweiId");
		}
		R wv = validateWindow(body.getKaishiShijian(), body.getJieshuShijian());
		if (wv != null) {
			return wv;
		}
		CheweiEntity probe = cheweiService.selectById(body.getCheweiId());
		if (probe == null) {
			return R.error("车位不存在");
		}
		String lot = nz(probe.getTingchechangmingcheng());
		String quyuNorm = normalizeQuyu(probe.getQuyu());
		if (StringUtils.isBlank(lot)) {
			return R.error("车位缺少停车场名称，无法做余位统计");
		}
		// 这是M4代码 — 锁定区域内所有车位行后再算余位，防止并发超卖
		List<CheweiEntity> spots = cheweiDao.selectListInScopeForUpdate(lot, quyuNorm);
		if (spots == null || spots.isEmpty()) {
			return R.error("该车场/区域下无车位主数据，请先维护车位");
		}
		CheweiEntity cw = null;
		for (CheweiEntity c : spots) {
			if (body.getCheweiId().equals(c.getId())) {
				cw = c;
				break;
			}
		}
		if (cw == null) {
			return R.error("目标车位不在该车场/区域下");
		}
		List<Long> ids = new ArrayList<Long>(spots.size());
		for (CheweiEntity c : spots) {
			ids.add(c.getId());
		}
		List<CheweiYuyueEntity> yuyues = listActiveYuyueForCheweiIds(ids);
		int available = countAvailableInScope(spots, body.getKaishiShijian(), body.getJieshuShijian(), yuyues);
		if (available < 1) {
			return m4YuweiBuzu("该时段车场/区域内可预约车位为 0（总 " + spots.size() + "）");
		}
		if (isSpotUnavailableInWindow(cw, body.getKaishiShijian(), body.getJieshuShijian(), yuyues)) {
			return m4YuweiBuzu("该车位在选定时段内已被占用");
		}
		if (hasOverlappingYuyue(cw.getId(), body.getKaishiShijian(), body.getJieshuShijian(), yuyues)) {
			return m4Chongtu("该车位时段与已有有效预约冲突");
		}
		String z = StringUtils.isBlank(cw.getZhuangtai()) ? CheweiZhuangtaiN2.KONGXIAN : cw.getZhuangtai().trim();
		if (!CheweiZhuangtaiN2.KONGXIAN.equals(z) && !CheweiZhuangtaiN2.YI_JIESUAN.equals(z)) {
			return m4Chongtu("仅「空闲」或「已结算」车位可预约，当前为「" + z + "」");
		}

		CheweiYuyueEntity row = new CheweiYuyueEntity();
		row.setCheweiId(cw.getId());
		row.setTingchechangmingcheng(lot);
		row.setQuyu(quyuNorm);
		row.setKaishiShijian(body.getKaishiShijian());
		row.setJieshuShijian(body.getJieshuShijian());
		row.setZhuangtai(CheweiYuyueZhuangtaiN4.YOUXIAO);
		row.setYonghuzhanghao(yonghuzhanghao.trim());
		row.setYuyueZhifuZhuangtai(YuyueZhifuZhuangtaiM1.WUXU_YUFU);
		row.setLiuchengJiedian(YuyueLiuchengJiedianM1.YIYUYUE_DAIRUCHANG);
		row.setAddtime(new Date());
		try {
			insert(row);
		} catch (DuplicateKeyException ex) {
			return m4Chongtu("重复预约请求，请勿重复提交");
		}

		cw.setZhuangtai(CheweiZhuangtaiN2.YUYUE_WEIRUCHANG);
		boolean updated = cheweiService.updateById(cw);
		if (!updated) {
			throw new IllegalStateException("车位状态已被其他用户修改，请刷新后重试");
		}
		CheweiEntity cwAfter = cheweiService.selectById(cw.getId());
		Map<String, Object> data = new HashMap<String, Object>(6);
		data.put("chewei", cwAfter != null ? cwAfter : cw);
		data.put("yuyue", row);
		data.put("availableAfter", available - 1);
		return R.ok().put("data", data);
	}

	@Override
	public void cancelActiveYuyueForChewei(Long cheweiId) {
		if (cheweiId == null) {
			return;
		}
		EntityWrapper<CheweiYuyueEntity> w = new EntityWrapper<CheweiYuyueEntity>();
		w.eq("chewei_id", cheweiId);
		w.eq("zhuangtai", CheweiYuyueZhuangtaiN4.YOUXIAO);
		List<CheweiYuyueEntity> list = selectList(w);
		for (CheweiYuyueEntity y : list) {
			y.setZhuangtai(CheweiYuyueZhuangtaiN4.YI_QUXIAO);
			y.setLiuchengJiedian(YuyueLiuchengJiedianM1.YI_QUXIAO);
			updateById(y);
		}
	}

	@Override
	public void m1SyncAfterRuchang(Long cheweiId, Long chezijinchangId, Date jinchang) {
		if (cheweiId == null || chezijinchangId == null || jinchang == null) {
			return;
		}
		EntityWrapper<CheweiYuyueEntity> w = new EntityWrapper<CheweiYuyueEntity>();
		w.eq("chewei_id", cheweiId);
		w.eq("zhuangtai", CheweiYuyueZhuangtaiN4.YOUXIAO);
		w.isNull("chezijinchang_id");
		w.le("kaishi_shijian", jinchang);
		w.gt("jieshu_shijian", jinchang);
		w.last("ORDER BY id DESC LIMIT 1");
		CheweiYuyueEntity y = selectOne(w);
		if (y == null) {
			return;
		}
		y.setChezijinchangId(chezijinchangId);
		y.setLiuchengJiedian(YuyueLiuchengJiedianM1.YIRUCHANG);
		updateById(y);
	}

	@Override
	public void m1BindRuchangToYuyue(Long yuyueId, Long expectedCheweiId, Long chezijinchangId, Date jinchang) {
		if (yuyueId == null || expectedCheweiId == null || chezijinchangId == null || jinchang == null) {
			throw new IllegalStateException("绑定预约缺少参数");
		}
		CheweiYuyueEntity y = selectById(yuyueId);
		if (y == null) {
			throw new IllegalStateException("预约单不存在");
		}
		if (!CheweiYuyueZhuangtaiN4.YOUXIAO.equals(nz(y.getZhuangtai()))) {
			throw new IllegalStateException("预约单无效，无法入场绑定");
		}
		if (y.getCheweiId() == null || !y.getCheweiId().equals(expectedCheweiId)) {
			throw new IllegalStateException("预约单与入场车位不一致");
		}
		if (y.getChezijinchangId() != null) {
			throw new IllegalStateException("预约单已关联入场单，不能重复入场");
		}
		String lj = nz(y.getLiuchengJiedian());
		if (StringUtils.isNotBlank(lj) && !YuyueLiuchengJiedianM1.YIYUYUE_DAIRUCHANG.equals(lj)) {
			throw new IllegalStateException("预约单流程节点须为「已预约待入场」，当前为「" + lj + "」");
		}
		if (y.getKaishiShijian() != null && jinchang.getTime() < y.getKaishiShijian().getTime()) {
			throw new IllegalStateException("未到预约时段开始时间，不允许入场");
		}
		if (y.getJieshuShijian() != null && jinchang.getTime() >= y.getJieshuShijian().getTime()) {
			throw new IllegalStateException("已超过预约时段结束时间，不允许入场");
		}
		y.setChezijinchangId(chezijinchangId);
		y.setLiuchengJiedian(YuyueLiuchengJiedianM1.YIRUCHANG);
		updateById(y);
	}

	@Override
	public void m1SyncAfterLichangOrder(Long cheweiId, Long chezijinchangId, Long tingchejiaofeiId) {
		if (cheweiId == null || chezijinchangId == null || tingchejiaofeiId == null) {
			return;
		}
		EntityWrapper<CheweiYuyueEntity> w = new EntityWrapper<CheweiYuyueEntity>();
		w.eq("chewei_id", cheweiId);
		w.eq("chezijinchang_id", chezijinchangId);
		w.eq("zhuangtai", CheweiYuyueZhuangtaiN4.YOUXIAO);
		w.isNull("tingchejiaofei_id");
		w.last("ORDER BY id DESC LIMIT 1");
		CheweiYuyueEntity y = selectOne(w);
		if (y == null) {
			return;
		}
		y.setTingchejiaofeiId(tingchejiaofeiId);
		y.setLiuchengJiedian(YuyueLiuchengJiedianM1.YILICHANG_DAIZHIFU);
		y.setYuyueZhifuZhuangtai(YuyueZhifuZhuangtaiM1.WEI_ZHIFU);
		updateById(y);
	}

	@Override
	public void m1SyncAfterParkingFeePaid(Long tingchejiaofeiId) {
		if (tingchejiaofeiId == null) {
			return;
		}
		EntityWrapper<CheweiYuyueEntity> w = new EntityWrapper<CheweiYuyueEntity>();
		w.eq("tingchejiaofei_id", tingchejiaofeiId);
		w.eq("zhuangtai", CheweiYuyueZhuangtaiN4.YOUXIAO);
		w.last("ORDER BY id DESC LIMIT 1");
		CheweiYuyueEntity y = selectOne(w);
		if (y == null) {
			return;
		}
		y.setLiuchengJiedian(YuyueLiuchengJiedianM1.YIWANCHENG);
		y.setYuyueZhifuZhuangtai(YuyueZhifuZhuangtaiM1.YI_ZHIFU);
		updateById(y);
	}

	@Override
	public R m1YuyueListByChewei(Long cheweiId) {
		if (cheweiId == null) {
			return R.error("须传入车位 id：cheweiId");
		}
		EntityWrapper<CheweiYuyueEntity> w = new EntityWrapper<CheweiYuyueEntity>();
		w.eq("chewei_id", cheweiId);
		w.orderBy("id", false);
		return R.ok().put("data", selectList(w));
	}
}
