package com.constant;

/**
 * 这是N10代码 — 异常类型编码。
 * 这是我cursor给父亲写的
 */
public final class N10YichangLeixing {

	private N10YichangLeixing() {
	}

	/** 有效预约、未关联入场（含 N6 保留期内待入场与已逾期仍未取消） */
	public static final String WEI_RUCHANG_YUYUE = "weiRuchangYuyue";
	/** 已离场、停车费未支付 */
	public static final String WEI_ZHIFU_LICHANG = "weiZhifuLichang";
	/** N7 待支付补缴单 */
	public static final String DAI_BUJIAO = "daiBujiao";
	/** 已入场但超过预约结束时间，尚无已支付超时补缴（N7） */
	public static final String CHAOSHI_WEI_XUFEI = "chaoshiWeiXufei";
	/** N6 超时未入场违约金待付 */
	public static final String WEIYUE_WEI_RUCHANG = "weiyueWeiRuchang";
}
