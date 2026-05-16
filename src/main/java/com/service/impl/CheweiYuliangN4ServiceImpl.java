package com.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.constant.CheweiYuyueZhuangtaiN4;
import com.constant.CheweiZhuangtaiN2;
import com.constant.YuyueLiuchengJiedianM1;
import com.constant.YuyueZhifuZhuangtaiM1;
import com.dao.CheweiYuyueDao;
import com.entity.CheweiEntity;
import com.entity.CheweiYuyueEntity;
import com.entity.dto.N4YuliangChaDto;
import com.entity.dto.N4YuyueReserveDto;
import com.service.CheweiService;
import com.service.CheweiYuliangN4Service;
import com.utils.R;

/**
 * 这是N4代码 — 余位与时段预约实现；含 M1 预约单与入场/离场/支付流水同步。这是我cursor给父亲写的
 */
@Service("cheweiYuliangN4Service")
public class CheweiYuliangN4ServiceImpl extends ServiceImpl<CheweiYuyueDao, CheweiYuyueEntity>
		implements CheweiYuliangN4Service {

	@Autowired
	private CheweiService cheweiService;

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
		return R.ok().put("data", data);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R reserveWithSlot(N4YuyueReserveDto body) {
		if (body == null || body.getCheweiId() == null) {
			return R.error("须传入车位 id：cheweiId");
		}
		R wv = validateWindow(body.getKaishiShijian(), body.getJieshuShijian());
		if (wv != null) {
			return wv;
		}
		CheweiEntity cw = cheweiService.selectById(body.getCheweiId());
		if (cw == null) {
			return R.error("车位不存在");
		}
		String lot = nz(cw.getTingchechangmingcheng());
		String quyuNorm = normalizeQuyu(cw.getQuyu());
		if (StringUtils.isBlank(lot)) {
			return R.error("车位缺少停车场名称，无法做余位统计");
		}
		List<CheweiEntity> spots = listCheweiInScope(lot, quyuNorm);
		List<Long> ids = new ArrayList<Long>(spots.size());
		for (CheweiEntity c : spots) {
			ids.add(c.getId());
		}
		List<CheweiYuyueEntity> yuyues = listActiveYuyueForCheweiIds(ids);
		int available = 0;
		for (CheweiEntity c : spots) {
			if (!isSpotUnavailableInWindow(c, body.getKaishiShijian(), body.getJieshuShijian(), yuyues)) {
				available++;
			}
		}
		if (available < 1) {
			return R.error("余位不足：该时段车场/区域内可预约车位为 0（总 " + spots.size() + "，已占用 " + (spots.size() - available)
					+ "），禁止下单");
		}
		if (isSpotUnavailableInWindow(cw, body.getKaishiShijian(), body.getJieshuShijian(), yuyues)) {
			return R.error("该车位在选定时段内不可用（已入场/待结算/已有预约或时段冲突）");
		}
		String z = StringUtils.isBlank(cw.getZhuangtai()) ? CheweiZhuangtaiN2.KONGXIAN : cw.getZhuangtai().trim();
		if (!CheweiZhuangtaiN2.KONGXIAN.equals(z) && !CheweiZhuangtaiN2.YI_JIESUAN.equals(z)) {
			return R.error("仅「空闲」或「已结算」车位可预约，当前为「" + z + "」");
		}

		CheweiYuyueEntity row = new CheweiYuyueEntity();
		row.setCheweiId(cw.getId());
		row.setTingchechangmingcheng(lot);
		row.setQuyu(quyuNorm);
		row.setKaishiShijian(body.getKaishiShijian());
		row.setJieshuShijian(body.getJieshuShijian());
		row.setZhuangtai(CheweiYuyueZhuangtaiN4.YOUXIAO);
		row.setYuyueZhifuZhuangtai(YuyueZhifuZhuangtaiM1.WUXU_YUFU);
		row.setLiuchengJiedian(YuyueLiuchengJiedianM1.YIYUYUE_DAIRUCHANG);
		row.setAddtime(new Date());
		insert(row);

		cw.setZhuangtai(CheweiZhuangtaiN2.YUYUE_WEIRUCHANG);
		cheweiService.updateById(cw);
		Map<String, Object> data = new HashMap<String, Object>(4);
		data.put("chewei", cw);
		data.put("yuyue", row);
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
