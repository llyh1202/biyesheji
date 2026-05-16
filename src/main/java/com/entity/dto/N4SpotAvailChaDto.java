package com.entity.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 这是我cursor给父亲写的 — 按车位查询时段可约：车场条件 + 预约时段
 */
public class N4SpotAvailChaDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long cheweixinxiId;
	private String tingchechangmingcheng;
	private String quyu;

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date kaishiShijian;

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date jieshuShijian;

	public Long getCheweixinxiId() {
		return cheweixinxiId;
	}

	public void setCheweixinxiId(Long cheweixinxiId) {
		this.cheweixinxiId = cheweixinxiId;
	}

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
