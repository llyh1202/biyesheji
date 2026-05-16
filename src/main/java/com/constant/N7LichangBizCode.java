package com.constant;

/**
 * 这是我cursor给父亲写的 — P1-23 离场（lichang）与 N7 补缴业务错误码
 */
public final class N7LichangBizCode {

	private N7LichangBizCode() {
	}

	/** 离场前存在待支付补缴单，须先完成 N7 补缴 */
	public static final int UNPAID_BUJIAO = 4707;

	public static final String BIZ_KEY = "N7_UNPAID_BUJIAO";
}
