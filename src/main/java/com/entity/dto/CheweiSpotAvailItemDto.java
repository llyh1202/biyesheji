package com.entity.dto;

import java.io.Serializable;

/**
 * 这是我cursor给父亲写的 — 单车位在指定时段的可约结果（供前端选位）
 */
public class CheweiSpotAvailItemDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String cheweibianhao;
	private String zhuangtai;
	private String quyu;
	/** 该时段是否可预约 */
	private Boolean keyuyue;
	/** 不可约时的原因说明；可约时为 null 或空 */
	private String reason;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCheweibianhao() {
		return cheweibianhao;
	}

	public void setCheweibianhao(String cheweibianhao) {
		this.cheweibianhao = cheweibianhao;
	}

	public String getZhuangtai() {
		return zhuangtai;
	}

	public void setZhuangtai(String zhuangtai) {
		this.zhuangtai = zhuangtai;
	}

	public String getQuyu() {
		return quyu;
	}

	public void setQuyu(String quyu) {
		this.quyu = quyu;
	}

	public Boolean getKeyuyue() {
		return keyuyue;
	}

	public void setKeyuyue(Boolean keyuyue) {
		this.keyuyue = keyuyue;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
