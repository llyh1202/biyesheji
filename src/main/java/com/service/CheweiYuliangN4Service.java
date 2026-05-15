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
}
