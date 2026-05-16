package com.service;

import com.entity.dto.N4YuliangChaDto;
import com.entity.dto.N4YuyueReserveDto;
import com.utils.R;

/**
 * 这是N4代码 — 预约前余位校验与时段预约落库。
 * 这是我cursor给父亲写的
 */
public interface CheweiYuliangN4Service {

	/**
	 * 统计车场/区域在给定时段内可预约余位数（总车位 − 已占用：已入场、待结算、已预约、有效时段预约重叠）。
	 */
	R availability(N4YuliangChaDto body);

	/**
	 * 余位足够且目标车位在时段内可用时：写入时段预约并置车位为「已预约未入场」。
	 */
	R reserveWithSlot(N4YuyueReserveDto body);

	/**
	 * 取消该车位的所有「有效」时段预约（供 N2 取消预约时联动）。
	 */
	void cancelActiveYuyueForChewei(Long cheweiId);

	/** 这是M1代码 — 入场后把有效时段预约单挂上入场单 id，并推进流程节点。这是我cursor给父亲写的 */
	void m1SyncAfterRuchang(Long cheweiId, Long chezijinchangId, java.util.Date jinchang);

	/**
	 * 这是M2代码 — 将指定预约单与入场单显式绑定（避免多预约重叠时误挂）；须与车位、时段、流程节点一致。
	 * 这是我cursor给父亲写的
	 */
	void m1BindRuchangToYuyue(Long yuyueId, Long expectedCheweiId, Long chezijinchangId, java.util.Date jinchang);

	/** 这是M1代码 — 生成离场/缴费单后挂 tingchejiaofei_id，节点进入待支付停车费。这是我cursor给父亲写的 */
	void m1SyncAfterLichangOrder(Long cheweiId, Long chezijinchangId, Long tingchejiaofeiId);

	/** 这是M1代码 — 停车费已支付后推进预约单节点与预约侧支付态。这是我cursor给父亲写的 */
	void m1SyncAfterParkingFeePaid(Long tingchejiaofeiId);

	/** 这是M1代码 — 查询某车位时段预约单列表（含扩展字段），按 id 倒序。这是我cursor给父亲写的 */
	R m1YuyueListByChewei(Long cheweiId);
}
