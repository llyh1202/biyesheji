package com.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.constant.CheweiZhuangtaiN2;
import com.entity.CheweiEntity;
import com.entity.ChezijinchangEntity;
import com.entity.TingchejiaofeiEntity;
import com.service.CheweiService;
import com.service.CheweiYuliangN4Service;
import com.service.CheweiZhuangtaiN2Service;
import com.service.ChezijinchangService;

/**
 * 这是N2代码 — 车位占用状态机实现；联动 M1 时段预约单流程与支付态；这是M3代码 — 支付成功与车位释放、订单完结强一致。
 * 这是我cursor给父亲写的
 */
@Service("cheweiZhuangtaiN2Service")
public class CheweiZhuangtaiN2ServiceImpl implements CheweiZhuangtaiN2Service {

	@Autowired
	private CheweiService cheweiService;
	@Autowired
	private ChezijinchangService chezijinchangService;
	@Autowired
	private CheweiYuliangN4Service cheweiYuliangN4Service;

	private static String nz(String s) {
		if (StringUtils.isBlank(s)) {
			return CheweiZhuangtaiN2.KONGXIAN;
		}
		return s.trim();
	}

	private Long resolveCheweiIdForOrder(TingchejiaofeiEntity order) {
		if (order.getCheweiId() != null) {
			return order.getCheweiId();
		}
		if (order.getCrossrefid() != null) {
			ChezijinchangEntity e = chezijinchangService.selectById(order.getCrossrefid());
			if (e != null && e.getCheweiId() != null) {
				return e.getCheweiId();
			}
		}
		return null;
	}

	private void occupyCheweiForRuchang(ChezijinchangEntity entry) {
		if (entry == null || entry.getCheweiId() == null) {
			return;
		}
		CheweiEntity cw = cheweiService.selectById(entry.getCheweiId());
		if (cw == null) {
			throw new IllegalStateException("车位不存在，无法关联入场单");
		}
		String z = nz(cw.getZhuangtai());
		if (!CheweiZhuangtaiN2.KONGXIAN.equals(z) && !CheweiZhuangtaiN2.YUYUE_WEIRUCHANG.equals(z)
				&& !CheweiZhuangtaiN2.YI_JIESUAN.equals(z)) {
			throw new IllegalStateException("当前车位状态为「" + z + "」，不允许入场（需为：空闲/已预约未入场/已结算）");
		}
		cw.setZhuangtai(CheweiZhuangtaiN2.YI_RUCHANG);
		cw.setChezijinchangId(entry.getId());
		cw.setTingchejiaofeiId(null);
		cheweiService.updateById(cw);
	}

	@Override
	public void afterChezijinchangInserted(ChezijinchangEntity entry) {
		occupyCheweiForRuchang(entry);
		if (entry == null || entry.getCheweiId() == null) {
			return;
		}
		java.util.Date jt = entry.getJinchangshijian() != null ? entry.getJinchangshijian() : new java.util.Date();
		cheweiYuliangN4Service.m1SyncAfterRuchang(entry.getCheweiId(), entry.getId(), jt);
	}

	@Override
	public void afterChezijinchangInsertedForM2(ChezijinchangEntity entry, Long yuyueId) {
		occupyCheweiForRuchang(entry);
		if (entry == null || entry.getCheweiId() == null || yuyueId == null) {
			throw new IllegalStateException("M2 入场缺少车位或预约单");
		}
		java.util.Date jt = entry.getJinchangshijian() != null ? entry.getJinchangshijian() : new java.util.Date();
		cheweiYuliangN4Service.m1BindRuchangToYuyue(yuyueId, entry.getCheweiId(), entry.getId(), jt);
	}

	@Override
	public void afterTingchejiaofeiInserted(TingchejiaofeiEntity order) {
		if (order == null || order.getLichangshijian() == null) {
			return;
		}
		Long cheweiId = resolveCheweiIdForOrder(order);
		if (cheweiId == null) {
			return;
		}
		CheweiEntity cw = cheweiService.selectById(cheweiId);
		if (cw == null) {
			return;
		}
		String z = nz(cw.getZhuangtai());
		if (!CheweiZhuangtaiN2.YI_RUCHANG.equals(z)) {
			throw new IllegalStateException("生成离场单时车位状态应为「已入场」，当前为「" + z + "」");
		}
		if (order.getCrossrefid() != null && cw.getChezijinchangId() != null
				&& !cw.getChezijinchangId().equals(order.getCrossrefid())) {
			throw new IllegalStateException("离场单关联的入场单与当前车位占用不一致");
		}
		cw.setZhuangtai(CheweiZhuangtaiN2.YI_LICHANG_DAI_JIESUAN);
		cw.setTingchejiaofeiId(order.getId());
		cheweiService.updateById(cw);
		if (order.getCrossrefid() != null) {
			cheweiYuliangN4Service.m1SyncAfterLichangOrder(cheweiId, order.getCrossrefid(), order.getId());
		}
	}

	@Override
	public void afterTingchejiaofeiUpdatedIfPaid(TingchejiaofeiEntity before, TingchejiaofeiEntity after) {
		if (before == null || after == null) {
			return;
		}
		String was = nz(before.getIspay());
		String now = nz(after.getIspay());
		if ("已支付".equals(now) && "已支付".equals(was)) {
			return;
		}
		if (!"已支付".equals(now)) {
			return;
		}
		// M3：支付完结前置，与 N3 离场闭环一致，禁止「只改 ispay」绕过业务态
		if (after.getLichangshijian() == null) {
			throw new IllegalStateException("支付成功前须先有离场时间；请完成离场算费后再支付或结算");
		}
		if (after.getCrossrefid() == null) {
			throw new IllegalStateException("缴费单须关联入场单 crossrefid 后方可释放车位占用（N3/M3 闭环）");
		}
		Long cheweiId = resolveCheweiIdForOrder(after);
		if (cheweiId == null) {
			throw new IllegalStateException("无法解析车位 id，不能释放占用：请补全 cheweiId 或 crossrefid 对应入场单");
		}
		CheweiEntity cw = cheweiService.selectById(cheweiId);
		if (cw == null) {
			throw new IllegalStateException("车位不存在，无法释放占用");
		}
		String z = nz(cw.getZhuangtai());
		if (CheweiZhuangtaiN2.YI_JIESUAN.equals(z)) {
			cheweiYuliangN4Service.m1SyncAfterParkingFeePaid(after.getId());
			return;
		}
		if (!CheweiZhuangtaiN2.YI_LICHANG_DAI_JIESUAN.equals(z)) {
			throw new IllegalStateException("支付成功时车位须为「已离场待结算」，当前为「" + z + "」；不可在已入场等状态下仅靠改 ispay 完结订单");
		}
		if (cw.getTingchejiaofeiId() == null || !cw.getTingchejiaofeiId().equals(after.getId())) {
			throw new IllegalStateException("当前车位绑定的缴费单与本单不一致，拒绝释放占用");
		}
		cheweiYuliangN4Service.m1SyncAfterParkingFeePaid(after.getId());
		cw.setZhuangtai(CheweiZhuangtaiN2.YI_JIESUAN);
		cw.setChezijinchangId(null);
		cw.setTingchejiaofeiId(null);
		cheweiService.updateById(cw);
	}
}
