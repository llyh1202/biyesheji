package com.entity.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 这是M5代码 — 统一计费试算/离场结算入参。
 * 这是我cursor给父亲写的
 */
public class M5JifeiCalcDto {

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date jinchangshijian;

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lichangshijian;

	private String tingchechangmingcheng;
	private String quyu;
	private Integer xiaoshidanjia;

	public Date getJinchangshijian() {
		return jinchangshijian;
	}

	public void setJinchangshijian(Date jinchangshijian) {
		this.jinchangshijian = jinchangshijian;
	}

	public Date getLichangshijian() {
		return lichangshijian;
	}

	public void setLichangshijian(Date lichangshijian) {
		this.lichangshijian = lichangshijian;
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

	public Integer getXiaoshidanjia() {
		return xiaoshidanjia;
	}

	public void setXiaoshidanjia(Integer xiaoshidanjia) {
		this.xiaoshidanjia = xiaoshidanjia;
	}
}
