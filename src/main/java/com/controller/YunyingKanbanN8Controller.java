package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service.YunyingKanbanN8Service;
import com.utils.R;

/**
 * 这是N8代码 — 运营总览看板 API。
 * 这是我cursor给父亲写的
 */
@RestController
@RequestMapping("/chewei/n8")
public class YunyingKanbanN8Controller {

	@Autowired
	private YunyingKanbanN8Service yunyingKanbanN8Service;

	/** 今日/本周 KPI 总览 */
	@RequestMapping("/kanban/overview")
	public R overview() {
		return yunyingKanbanN8Service.overview();
	}
}
