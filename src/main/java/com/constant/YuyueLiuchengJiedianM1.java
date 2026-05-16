package com.constant;

/**
 * 这是M1代码 — 预约单在「预约—入场—离场—支付」链路上的当前节点（与 chewei.zhuangtai N2 对齐语义）。
 * 这是N6代码 — 增加超时未入场相关节点。
 * 这是我cursor给父亲写的
 */
public final class YuyueLiuchengJiedianM1 {

	private YuyueLiuchengJiedianM1() {
	}

	public static final String YIYUYUE_DAIRUCHANG = "已预约待入场";
	public static final String YIRUCHANG = "已入场";
	public static final String YILICHANG_DAIZHIFU = "已离场待支付停车费";
	public static final String YIWANCHENG = "已完成";
	public static final String YI_QUXIAO = "已取消";
	/** 这是N6代码 — 定时任务：超过预约保留时长未入场，已作废预约 */
	public static final String CHAOSHI_ZIDONG_QUXIAO = "超时自动取消";
	/** 这是N6代码 — 超时未入场且规则配置了违约金，已生成违约缴费单待支付 */
	public static final String CHAOSHI_WEIRUCHANG_DAIFU = "超时未入场待付违约";
}
