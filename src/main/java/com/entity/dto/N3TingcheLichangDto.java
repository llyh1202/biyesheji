package com.entity.dto;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 这是N3代码 — 离场业务请求体：必须关联入场单。
 * 这是我cursor给父亲写的
 */
public class N3TingcheLichangDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long chezijinchangId;

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lichangshijian;

	public Long getChezijinchangId() {
		return chezijinchangId;
	}

	public void setChezijinchangId(Long chezijinchangId) {
		this.chezijinchangId = chezijinchangId;
	}

	public Date getLichangshijian() {
		return lichangshijian;
	}

	public void setLichangshijian(Date lichangshijian) {
		this.lichangshijian = lichangshijian;
	}
}
