package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annotation.IgnoreAuth;
import com.entity.CheweiJifeiGuizeEntity;
import com.entity.dto.M5JifeiCalcDto;
import com.service.TingcheJifeiM5Service;
import com.utils.R;

/**
 * 这是M5代码 — 费用计算规则维护与统一试算。
 * 这是我cursor给父亲写的
 */
@RestController
@RequestMapping("/chewei/m5")
public class TingcheJifeiM5Controller {

	@Autowired
	private TingcheJifeiM5Service tingcheJifeiM5Service;

	@IgnoreAuth
	@RequestMapping("/guize/list")
	public R guizeList() {
		return tingcheJifeiM5Service.listGuize();
	}

	@RequestMapping("/guize/save")
	public R guizeSave(@RequestBody CheweiJifeiGuizeEntity row) {
		return tingcheJifeiM5Service.saveGuize(row);
	}

	@RequestMapping("/guize/update")
	public R guizeUpdate(@RequestBody CheweiJifeiGuizeEntity row) {
		return tingcheJifeiM5Service.updateGuize(row);
	}

	@RequestMapping("/guize/delete")
	public R guizeDelete(@RequestBody Long[] ids) {
		if (ids == null || ids.length == 0) {
			return R.error("须传入 id 数组");
		}
		for (Long id : ids) {
			tingcheJifeiM5Service.deleteGuize(id);
		}
		return R.ok();
	}

	@IgnoreAuth
	@RequestMapping("/guize/resolve")
	public R guizeResolve(@RequestParam(value = "tingchechangmingcheng", required = false) String lot,
			@RequestParam(value = "quyu", required = false) String quyu) {
		return tingcheJifeiM5Service.resolveRuleSnapshot(lot, quyu);
	}

	/** 统一计费试算（离场前预览 / N7 参考） */
	@IgnoreAuth
	@RequestMapping("/jifei/calc")
	public R jifeiCalc(@RequestBody M5JifeiCalcDto body) {
		return tingcheJifeiM5Service.calculate(body);
	}
}
