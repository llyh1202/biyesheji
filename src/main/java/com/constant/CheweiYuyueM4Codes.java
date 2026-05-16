package com.constant;

/**
 * 这是M4代码 — 预约接口校验错误码（前端可据此提示「余位不足」等）。
 * 这是我cursor给父亲写的
 */
public final class CheweiYuyueM4Codes {

	private CheweiYuyueM4Codes() {
	}

	/** 车场/区域或目标车位在锁定后无余位 */
	public static final int CODE_YUWEI_BUZU = 4601;
	public static final String MSG_YUWEI_BUZU = "余位不足";

	/** 车位状态/版本冲突、时段冲突 */
	public static final int CODE_CHONGTU = 4602;
	public static final String KEY_YUWEI_BUZU = "YUWEI_BUZU";
	public static final String KEY_CHONGTU = "CHONGTU";
}
