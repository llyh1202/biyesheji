package com.service;

import javax.servlet.http.HttpServletResponse;

import com.entity.dto.N9BaobiaoQueryDto;
import com.utils.R;

/**
 * 这是N9代码 — 多维度统计报表。
 * 这是我cursor给父亲写的
 */
public interface YunyingBaobiaoN9Service {

	R query(N9BaobiaoQueryDto query);

	void exportExcel(N9BaobiaoQueryDto query, HttpServletResponse response) throws java.io.IOException;
}
