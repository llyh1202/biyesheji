package com.controller.stat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utils.R;

/**
 * 这是M7代码 — 统计接口目录（规范入口说明，便于前端统一对接 /chewei/stat/*）。
 * 这是我cursor给父亲写的
 */
@RestController
@RequestMapping("/chewei/stat")
public class CheweiStatCatalogM7Controller {

	@RequestMapping("/catalog")
	public R catalog() {
		List<Map<String, String>> apis = new ArrayList<Map<String, String>>();
		apis.add(entry("GET", "/chewei/stat/kanban/overview", "运营看板 KPI（N8）"));
		apis.add(entry("POST/GET", "/chewei/stat/baobiao/query", "多维报表查询（N9）"));
		apis.add(entry("GET", "/chewei/stat/baobiao/exportExcel", "多维报表导出（N9）"));
		apis.add(entry("GET", "/chewei/stat/yichang/report", "异常报表（N10）"));
		apis.add(entry("GET", "/chewei/stat/home/charts", "首页收入/周转图（M6）"));
		apis.add(entry("GET", "/chewei/stat/catalog", "本目录"));
		Map<String, Object> spec = new LinkedHashMap<String, Object>();
		spec.put("package", "com.controller.stat");
		spec.put("readDao", "com.dao.stat.CheweiTongjiReadDao");
		spec.put("views", new String[] { "v_stat_chewei_dim", "v_stat_chewei_global", "v_stat_revenue_daily" });
		spec.put("note", "旧路径 /chewei/n8|n9|n10|m6 仍兼容；新报表请优先使用 /chewei/stat/*");
		return R.ok().put("apis", apis).put("spec", spec);
	}

	private static Map<String, String> entry(String method, String path, String desc) {
		Map<String, String> m = new LinkedHashMap<String, String>();
		m.put("method", method);
		m.put("path", path);
		m.put("desc", desc);
		return m;
	}
}
