package com.service;

import com.utils.R;

/**
 * 这是我cursor给父亲写的 — P1-08 我的停车汇总
 */
public interface CheweiMyParkingP1Service {

	/** 待入场预约 + 在场入场单 + 待支付缴费单 */
	R myParkingSummary(String yonghuzhanghao);
}
