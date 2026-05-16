package com.entity.dto;

/**
 * 这是N7代码 — 管理员创建补缴/调整单。
 * 这是我cursor给父亲写的
 */
public class N7AdminBujiaoDto {

	private Long chezijinchangId;
	private Double jine;
	private String leixing;
	private String yuanyin;
	private String beizhu;
	private String guanliyuanZhanghao;

	public Long getChezijinchangId() {
		return chezijinchangId;
	}

	public void setChezijinchangId(Long chezijinchangId) {
		this.chezijinchangId = chezijinchangId;
	}

	public Double getJine() {
		return jine;
	}

	public void setJine(Double jine) {
		this.jine = jine;
	}

	public String getLeixing() {
		return leixing;
	}

	public void setLeixing(String leixing) {
		this.leixing = leixing;
	}

	public String getYuanyin() {
		return yuanyin;
	}

	public void setYuanyin(String yuanyin) {
		this.yuanyin = yuanyin;
	}

	public String getBeizhu() {
		return beizhu;
	}

	public void setBeizhu(String beizhu) {
		this.beizhu = beizhu;
	}

	public String getGuanliyuanZhanghao() {
		return guanliyuanZhanghao;
	}

	public void setGuanliyuanZhanghao(String guanliyuanZhanghao) {
		this.guanliyuanZhanghao = guanliyuanZhanghao;
	}
}
