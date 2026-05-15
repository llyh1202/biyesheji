package com.entity.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 这是N4代码 — 余位查询：车场/区域 + 时段（按天可把结束设为当日 23:59:59）。
 * 这是我cursor给父亲写的
 */
public class N4YuliangChaDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private String tingchechangmingcheng;
	private String quyu;

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date kaishiShijian;

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date jieshuShijian;

	public String getTingchechangmingcheng() {
		return tingchechangmingcheng;
	}

	public void setTingchechangmingcheng(String tingchechangmingcheng) {
		this.tingchechangmingcheng = tingchechangmingcheng;
	}

	public String getQuyu() {
		return quyu;
	}

	public void setQuyu(String quyu) {
		this.quyu = quyu;
	}

	public Date getKaishiShijian() {
		return kaishiShijian;
	}

	public void setKaishiShijian(Date kaishiShijian) {
		this.kaishiShijian = kaishiShijian;
	}

	public Date getJieshuShijian() {
		return jieshuShijian;
	}

	public void setJieshuShijian(Date jieshuShijian) {
		this.jieshuShijian = jieshuShijian;
	}
}
