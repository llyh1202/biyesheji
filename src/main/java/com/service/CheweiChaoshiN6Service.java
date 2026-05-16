package com.service;

import com.entity.CheweiChaoshiGuizeEntity;
import com.utils.R;

/**
 * 这是N6代码 — 超时策略：规则解析、定时扫描、离场计费宽限期。
 * 这是我cursor给父亲写的
 */
public interface CheweiChaoshiN6Service {

	/** 按车场/区域解析生效规则（先精确后全局默认） */
	CheweiChaoshiGuizeEntity resolveRule(String tingchechangmingcheng, String quyu);

	/** 离场计费宽限期（分钟），无规则时默认 15 */
	int resolveJifeiKuanxianFenzhong(String tingchechangmingcheng, String quyu);

	/** 定时/手动：扫描超时未入场预约并取消（可选违约金单） */
	R processScheduledTimeouts();

	R listGuize();

	R saveGuize(CheweiChaoshiGuizeEntity row);

	R updateGuize(CheweiChaoshiGuizeEntity row);

	R deleteGuize(Long id);

	/** 查询当前车场/区域将生效的规则快照 */
	R resolveRuleSnapshot(String tingchechangmingcheng, String quyu);
}
