package com.service;

import java.util.Date;

import com.entity.CheweiJifeiGuizeEntity;
import com.entity.dto.M5JifeiCalcDto;
import com.utils.R;

/**
 * 这是M5代码 — 停车费用规则解析与统一计费（N3 离场 / N6 宽限 / N7 试算共用）。
 * 这是我cursor给父亲写的
 */
public interface TingcheJifeiM5Service {

	CheweiJifeiGuizeEntity resolveRule(String tingchechangmingcheng, String quyu);

	R listGuize();

	R saveGuize(CheweiJifeiGuizeEntity row);

	R updateGuize(CheweiJifeiGuizeEntity row);

	R deleteGuize(Long id);

	R resolveRuleSnapshot(String tingchechangmingcheng, String quyu);

	/** 统一计费入口：含 N6 宽限期扣减 + M5 首小时/阶梯/封顶 */
	R calculate(M5JifeiCalcDto body);

	/** 便捷方法：离场结算调用 */
	R calculateParkingFee(Date jinchang, Date lichang, String lot, String quyu, Integer entryHourlyPrice);
}
