package com.controller.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service.YunyingKanbanN8Service;
import com.utils.R;

/**
 * 这是M7代码 — 运营看板统计专用 Controller（/chewei/stat/kanban/*），与业务 CheweiController 分离。
 * 这是我cursor给父亲写的
 */
@RestController
@RequestMapping("/chewei/stat/kanban")
public class CheweiStatKanbanM7Controller {

	@Autowired
	private YunyingKanbanN8Service yunyingKanbanN8Service;

	@RequestMapping("/overview")
	public R overview() {
		return yunyingKanbanN8Service.overview();
	}
}
