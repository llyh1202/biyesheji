package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service.YunyingYichangN10Service;
import com.utils.R;

/**
 * 这是N10代码 — 异常报表 API。
 * 这是我cursor给父亲写的
 */
@RestController
@RequestMapping("/chewei/n10")
public class YunyingYichangN10Controller {

	@Autowired
	private YunyingYichangN10Service yunyingYichangN10Service;

	@RequestMapping("/yichang/report")
	public R report() {
		return yunyingYichangN10Service.report();
	}
}
