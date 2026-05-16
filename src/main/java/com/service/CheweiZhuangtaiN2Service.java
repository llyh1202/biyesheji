package com.service;

import com.entity.ChezijinchangEntity;
import com.entity.TingchejiaofeiEntity;

/**
 * 这是N2代码 — 车位状态与入场单、缴费单联动。
 * 这是我cursor给父亲写的
 */
public interface CheweiZhuangtaiN2Service {

	/**
	 * 入场单保存成功后：占用车位为「已入场」。
	 *
	 * @throws IllegalStateException 车位状态不允许入场时
	 */
	void afterChezijinchangInserted(ChezijinchangEntity entry);

	/**
	 * 这是M2代码 — 入场后占用车位，并将「指定预约单」绑定到该入场单（不走按时段自动匹配）。
	 *
	 * @throws IllegalStateException 车位状态或预约绑定不允许时
	 */
	void afterChezijinchangInsertedForM2(ChezijinchangEntity entry, Long yuyueId);

	/**
	 * 离场/缴费单生成后：车位进入「已离场待结算」。
	 */
	void afterTingchejiaofeiInserted(TingchejiaofeiEntity order);

	/**
	 * 缴费单更新后：若从未支付变为已支付，则车位进入「已结算」并解除占用关联。
	 */
	void afterTingchejiaofeiUpdatedIfPaid(TingchejiaofeiEntity before, TingchejiaofeiEntity after);
}
