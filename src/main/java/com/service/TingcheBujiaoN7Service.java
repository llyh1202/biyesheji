package com.service;

import com.entity.TingcheBujiaoEntity;
import com.entity.dto.N7AdminBujiaoDto;
import com.utils.R;

/**
 * 这是N7代码 — 续费/超时补缴：管理员建单、用户支付、离场并入主单。
 * 这是我cursor给父亲写的
 */
public interface TingcheBujiaoN7Service {

	R adminCreate(N7AdminBujiaoDto body, String sessionAdminUsername);

	R userPay(Long id, String sessionUsername);

	R cancel(Long id, String sessionAdminUsername);

	R listByChezijinchang(Long chezijinchangId);

	R listByUser(String yonghuzhanghao);

	R overtimeCheck(Long chezijinchangId);

	/** 离场前：是否存在待支付补缴 */
	void assertNoUnpaidBeforeLichang(Long chezijinchangId);

	/**
	 * 这是我cursor给父亲写的 — P1-23 离场前 N7 校验：无待支付返回 null；否则返回错误码+补缴单列表
	 */
	R buildUnpaidBujiaoBlockLichang(Long chezijinchangId);

	/** 离场建主单后：已支付补缴并入主单金额 */
	double mergePaidIntoLichangOrder(Long chezijinchangId, Long tingchejiaofeiId, double baseFee);
}
