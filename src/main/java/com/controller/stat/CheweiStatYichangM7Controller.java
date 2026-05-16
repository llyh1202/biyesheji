package com.controller.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service.YunyingYichangN10Service;
import com.utils.R;

/**
 * 这是M7代码 — 异常报表统计专用 Controller（/chewei/stat/yichang/*）。
 * 这是我cursor给父亲写的
 */
@RestController
@RequestMapping("/chewei/stat/yichang")
public class CheweiStatYichangM7Controller {

	@Autowired
	private YunyingYichangN10Service yunyingYichangN10Service;

	@RequestMapping("/report")
	public R report() {
		return yunyingYichangN10Service.report();
	}
}
