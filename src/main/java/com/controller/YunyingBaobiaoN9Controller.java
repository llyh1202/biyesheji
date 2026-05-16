package com.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.entity.dto.N9BaobiaoQueryDto;
import com.service.YunyingBaobiaoN9Service;
import com.utils.R;

/**
 * 这是N9代码 — 多维度统计报表 API。
 * 这是我cursor给父亲写的
 */
@RestController
@RequestMapping("/chewei/n9")
public class YunyingBaobiaoN9Controller {

	@Autowired
	private YunyingBaobiaoN9Service yunyingBaobiaoN9Service;

	@RequestMapping("/baobiao/query")
	public R query(@RequestBody(required = false) N9BaobiaoQueryDto body,
			@RequestParam(value = "tingchechangmingcheng", required = false) String lot,
			@RequestParam(value = "quyu", required = false) String quyu,
			@RequestParam(value = "kaishiRiqi", required = false) String kaishiRiqi,
			@RequestParam(value = "jieshuRiqi", required = false) String jieshuRiqi) {
		N9BaobiaoQueryDto q = body != null ? body : new N9BaobiaoQueryDto();
		if (lot != null) {
			q.setTingchechangmingcheng(lot);
		}
		if (quyu != null) {
			q.setQuyu(quyu);
		}
		if (kaishiRiqi != null) {
			q.setKaishiRiqi(kaishiRiqi);
		}
		if (jieshuRiqi != null) {
			q.setJieshuRiqi(jieshuRiqi);
		}
		return yunyingBaobiaoN9Service.query(q);
	}

	/** 导出 Excel（维度汇总 + 收入趋势） */
	@RequestMapping("/baobiao/exportExcel")
	public void exportExcel(@RequestParam(value = "tingchechangmingcheng", required = false) String lot,
			@RequestParam(value = "quyu", required = false) String quyu,
			@RequestParam(value = "kaishiRiqi", required = false) String kaishiRiqi,
			@RequestParam(value = "jieshuRiqi", required = false) String jieshuRiqi,
			HttpServletResponse response) throws Exception {
		N9BaobiaoQueryDto q = new N9BaobiaoQueryDto();
		q.setTingchechangmingcheng(lot);
		q.setQuyu(quyu);
		q.setKaishiRiqi(kaishiRiqi);
		q.setJieshuRiqi(jieshuRiqi);
		yunyingBaobiaoN9Service.exportExcel(q, response);
	}
}
