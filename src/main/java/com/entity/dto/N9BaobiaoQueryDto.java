package com.entity.dto;

/**
 * 这是N9代码 — 多维度统计报表查询条件。
 * 这是我cursor给父亲写的
 */
public class N9BaobiaoQueryDto {

	private String tingchechangmingcheng;
	private String quyu;
	/** yyyy-MM-dd */
	private String kaishiRiqi;
	/** yyyy-MM-dd */
	private String jieshuRiqi;

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

	public String getKaishiRiqi() {
		return kaishiRiqi;
	}

	public void setKaishiRiqi(String kaishiRiqi) {
		this.kaishiRiqi = kaishiRiqi;
	}

	public String getJieshuRiqi() {
		return jieshuRiqi;
	}

	public void setJieshuRiqi(String jieshuRiqi) {
		this.jieshuRiqi = jieshuRiqi;
	}
}
