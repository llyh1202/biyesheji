package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.entity.CheweiChaoshiGuizeEntity;
import com.service.CheweiChaoshiN6Service;
import com.utils.R;

/**
 * 这是N6代码 — 超时策略：规则维护、生效规则查询、手动触发定时扫描。
 * 这是我cursor给父亲写的
 */
@RestController
@RequestMapping("/chewei/n6")
public class CheweiChaoshiN6Controller {

	@Autowired
	private CheweiChaoshiN6Service cheweiChaoshiN6Service;

	@RequestMapping("/guize/list")
	public R guizeList() {
		return cheweiChaoshiN6Service.listGuize();
	}

	@RequestMapping("/guize/save")
	public R guizeSave(@RequestBody CheweiChaoshiGuizeEntity row) {
		return cheweiChaoshiN6Service.saveGuize(row);
	}

	@RequestMapping("/guize/update")
	public R guizeUpdate(@RequestBody CheweiChaoshiGuizeEntity row) {
		return cheweiChaoshiN6Service.updateGuize(row);
	}

	@RequestMapping("/guize/delete")
	public R guizeDelete(@RequestBody Long[] ids) {
		if (ids == null || ids.length == 0) {
			return R.error("须传入 id 数组");
		}
		for (Long id : ids) {
			cheweiChaoshiN6Service.deleteGuize(id);
		}
		return R.ok();
	}

	/** 解析某车场/区域当前将生效的超时规则 */
	@RequestMapping("/guize/resolve")
	public R guizeResolve(@RequestParam(value = "tingchechangmingcheng", required = false) String lot,
			@RequestParam(value = "quyu", required = false) String quyu) {
		return cheweiChaoshiN6Service.resolveRuleSnapshot(lot, quyu);
	}

	/** 手动执行一次超时扫描（与定时任务逻辑相同） */
	@RequestMapping("/timeout/run")
	public R timeoutRun() {
		return cheweiChaoshiN6Service.processScheduledTimeouts();
	}
}
