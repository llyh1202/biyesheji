package com.controller.stat;

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
 * 这是M7代码 — 多维报表统计专用 Controller（/chewei/stat/baobiao/*）。
 * 这是我cursor给父亲写的
 */
@RestController
@RequestMapping("/chewei/stat/baobiao")
public class CheweiStatBaobiaoM7Controller {

	@Autowired
	private YunyingBaobiaoN9Service yunyingBaobiaoN9Service;

	@RequestMapping("/query")
	public R query(@RequestBody(required = false) N9BaobiaoQueryDto body,
			@RequestParam(value = "tingchechangmingcheng", required = false) String lot,
			@RequestParam(value = "quyu", required = false) String quyu,
			@RequestParam(value = "kaishiRiqi", required = false) String kaishiRiqi,
			@RequestParam(value = "jieshuRiqi", required = false) String jieshuRiqi) {
		return yunyingBaobiaoN9Service.query(mergeQuery(body, lot, quyu, kaishiRiqi, jieshuRiqi));
	}

	@RequestMapping("/exportExcel")
	public void exportExcel(@RequestBody(required = false) N9BaobiaoQueryDto body,
			@RequestParam(value = "tingchechangmingcheng", required = false) String lot,
			@RequestParam(value = "quyu", required = false) String quyu,
			@RequestParam(value = "kaishiRiqi", required = false) String kaishiRiqi,
			@RequestParam(value = "jieshuRiqi", required = false) String jieshuRiqi,
			HttpServletResponse response) throws java.io.IOException {
		yunyingBaobiaoN9Service.exportExcel(mergeQuery(body, lot, quyu, kaishiRiqi, jieshuRiqi), response);
	}

	private static N9BaobiaoQueryDto mergeQuery(N9BaobiaoQueryDto body, String lot, String quyu, String kaishiRiqi,
			String jieshuRiqi) {
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
		return q;
	}
}
