package com.entity.dto;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 这是N3代码 — 离场业务请求体：必须关联入场单。这是M2代码 — 可传 yuyueId 与预约单交叉校验。
 * 这是我cursor给父亲写的
 */
public class N3TingcheLichangDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long chezijinchangId;

	/** 这是M2代码 — 若传入则校验与预约单上的入场单、车位一致（闭环离场）。 */
	private Long yuyueId;

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lichangshijian;

	public Long getChezijinchangId() {
		return chezijinchangId;
	}

	public void setChezijinchangId(Long chezijinchangId) {
		this.chezijinchangId = chezijinchangId;
	}

	public Long getYuyueId() {
		return yuyueId;
	}

	public void setYuyueId(Long yuyueId) {
		this.yuyueId = yuyueId;
	}

	public Date getLichangshijian() {
		return lichangshijian;
	}

	public void setLichangshijian(Date lichangshijian) {
		this.lichangshijian = lichangshijian;
	}
}
