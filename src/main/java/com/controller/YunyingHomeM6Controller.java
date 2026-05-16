package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service.YunyingHomeM6Service;
import com.utils.R;

/**
 * 这是M6代码 — 管理端首页图表扩展 API。
 * 这是我cursor给父亲写的
 */
@RestController
@RequestMapping("/chewei/m6")
public class YunyingHomeM6Controller {

	@Autowired
	private YunyingHomeM6Service yunyingHomeM6Service;

	@RequestMapping("/home/charts")
	public R homeCharts() {
		return yunyingHomeM6Service.homeCharts();
	}
}
