package com.entity.dto;

import java.io.Serializable;

/**
 * 这是N3代码 — 结算关单请求体。
 * 这是我cursor给父亲写的
 */
public class N3TingcheJiesuanDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long tingchejiaofeiId;

	public Long getTingchejiaofeiId() {
		return tingchejiaofeiId;
	}

	public void setTingchejiaofeiId(Long tingchejiaofeiId) {
		this.tingchejiaofeiId = tingchejiaofeiId;
	}
}
