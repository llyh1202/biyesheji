package com.entity.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 这是N4代码 — 带时段的预约下单请求体。
 * 这是我cursor给父亲写的
 */
public class N4YuyueReserveDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long cheweiId;

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date kaishiShijian;

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date jieshuShijian;

	public Long getCheweiId() {
		return cheweiId;
	}

	public void setCheweiId(Long cheweiId) {
		this.cheweiId = cheweiId;
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
