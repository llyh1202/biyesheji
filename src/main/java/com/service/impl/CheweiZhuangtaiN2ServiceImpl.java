package com.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.constant.CheweiZhuangtaiN2;
import com.entity.CheweiEntity;
import com.entity.ChezijinchangEntity;
import com.entity.TingchejiaofeiEntity;
import com.service.CheweiService;
import com.service.CheweiZhuangtaiN2Service;
import com.service.ChezijinchangService;

/**
 * 这是N2代码 — 车位占用状态机实现。
 * 这是我cursor给父亲写的
 */
@Service("cheweiZhuangtaiN2Service")
public class CheweiZhuangtaiN2ServiceImpl implements CheweiZhuangtaiN2Service {

	@Autowired
	private CheweiService cheweiService;
	@Autowired
	private ChezijinchangService chezijinchangService;

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

	@Override
	public void afterChezijinchangInserted(ChezijinchangEntity entry) {
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
		Long cheweiId = resolveCheweiIdForOrder(after);
		if (cheweiId == null) {
			return;
		}
		CheweiEntity cw = cheweiService.selectById(cheweiId);
		if (cw == null) {
			return;
		}
		String z = nz(cw.getZhuangtai());
		if (!CheweiZhuangtaiN2.YI_LICHANG_DAI_JIESUAN.equals(z)) {
			// 允许管理员手工改支付后状态不一致时仍释放
			if (!CheweiZhuangtaiN2.YI_RUCHANG.equals(z)) {
				return;
			}
		}
		cw.setZhuangtai(CheweiZhuangtaiN2.YI_JIESUAN);
		cw.setChezijinchangId(null);
		cw.setTingchejiaofeiId(null);
		cheweiService.updateById(cw);
	}
}
