package com.entity.dto;

import java.io.Serializable;

/**
 * 这是我cursor给父亲写的 — 按车场查询车位列表项（预约选位展示）
 */
public class CheweiByLotItemDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String cheweibianhao;
	private String zhuangtai;
	private String quyu;

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
}
