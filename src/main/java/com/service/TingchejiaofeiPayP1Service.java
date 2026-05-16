package com.service;

import com.utils.R;

/**
 * 这是我cursor给父亲写的 — P1-19 用户端停车费支付完成（禁止直接 update ispay）
 */
public interface TingchejiaofeiPayP1Service {

	/**
	 * 缴费单支付完成：校验本人、已离场，改 ispay 并走 N2 afterTingchejiaofeiUpdatedIfPaid
	 */
	R payComplete(Long tingchejiaofeiId, String sessionYonghuzhanghao);
}
