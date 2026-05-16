package com.constant;

/**
 * 这是M1代码 — 时段预约单上的「预约侧/关联订单」支付状态文案（与 tingchejiaofei.ispay 区分：本字段侧重预约链路）。
 * 这是我cursor给父亲写的
 */
public final class YuyueZhifuZhuangtaiM1 {

	private YuyueZhifuZhuangtaiM1() {
	}

	/** 预约定金或关联停车费未结 */
	public static final String WEI_ZHIFU = "未支付";
	public static final String YI_ZHIFU = "已支付";
	/** 当前业务未接预约定金，仅占位时段时默认 */
	public static final String WUXU_YUFU = "无需预付";
}
