package com.constant;

/**
 * 这是N2代码 — N2 车位占用状态机：合法状态文案与说明。
 * 这是我cursor给父亲写的
 */
public final class CheweiZhuangtaiN2 {

	private CheweiZhuangtaiN2() {
	}

	/** 无车、无在途订单 */
	public static final String KONGXIAN = "空闲";
	/** 已预约，尚未生成入场单或未关联入场 */
	public static final String YUYUE_WEIRUCHANG = "已预约未入场";
	/** 已关联入场单，车辆在场 */
	public static final String YI_RUCHANG = "已入场";
	/** 已生成离场/缴费单且未支付 */
	public static final String YI_LICHANG_DAI_JIESUAN = "已离场待结算";
	/** 已支付完成（车位可再次分配前保留该态，再次入场时自动转入已入场） */
	public static final String YI_JIESUAN = "已结算";

	public static String[] allStates() {
		return new String[] { KONGXIAN, YUYUE_WEIRUCHANG, YI_RUCHANG, YI_LICHANG_DAI_JIESUAN, YI_JIESUAN };
	}
}
