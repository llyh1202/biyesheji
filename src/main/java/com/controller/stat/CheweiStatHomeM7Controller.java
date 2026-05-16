package com.controller.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service.YunyingHomeM6Service;
import com.utils.R;

/**
 * 这是M7代码 — 首页图表统计专用 Controller（/chewei/stat/home/*）。
 * 这是我cursor给父亲写的
 */
@RestController
@RequestMapping("/chewei/stat/home")
public class CheweiStatHomeM7Controller {

	@Autowired
	private YunyingHomeM6Service yunyingHomeM6Service;

	@RequestMapping("/charts")
	public R charts() {
		return yunyingHomeM6Service.homeCharts();
	}
}
