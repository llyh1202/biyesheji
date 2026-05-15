package com.service;

import com.utils.R;

/**
 * 这是N5代码 — 车位可视化：图例与区域下车位列表（含色块、可选栅格坐标）。
 * 这是我cursor给父亲写的
 */
public interface CheweiKeshihuaN5Service {

	/** 状态 → 展示色（图例） */
	R legend();

	/**
	 * 某停车场 + 区域下的全部车位，用于列表/栅格图；按栅格坐标优先、再按车位编号排序。
	 */
	R slots(String tingchechangmingcheng, String quyu);
}
