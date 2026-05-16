package com.entity.dto;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 这是M2代码 — 按预约单入场的请求体：先读预约、校验车位与时段，再写入场单并绑定该预约。
 * 这是我cursor给父亲写的
 */
public class M2RuchangDto implements Serializable {
	private static final long serialVersionUID = 1L;

	/** 时段预约单 id（chewei_yuyue.id） */
	private Long yuyueId;

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date jinchangshijian;

	private String yonghuzhanghao;
	private String xingming;
	private String shouji;
	private String chepaihao;
	private String cheliangtupian;

	public Long getYuyueId() {
		return yuyueId;
	}

	public void setYuyueId(Long yuyueId) {
		this.yuyueId = yuyueId;
	}

	public Date getJinchangshijian() {
		return jinchangshijian;
	}

	public void setJinchangshijian(Date jinchangshijian) {
		this.jinchangshijian = jinchangshijian;
	}

	public String getYonghuzhanghao() {
		return yonghuzhanghao;
	}

	public void setYonghuzhanghao(String yonghuzhanghao) {
		this.yonghuzhanghao = yonghuzhanghao;
	}

	public String getXingming() {
		return xingming;
	}

	public void setXingming(String xingming) {
		this.xingming = xingming;
	}

	public String getShouji() {
		return shouji;
	}

	public void setShouji(String shouji) {
		this.shouji = shouji;
	}

	public String getChepaihao() {
		return chepaihao;
	}

	public void setChepaihao(String chepaihao) {
		this.chepaihao = chepaihao;
	}

	public String getCheliangtupian() {
		return cheliangtupian;
	}

	public void setCheliangtupian(String cheliangtupian) {
		this.cheliangtupian = cheliangtupian;
	}
}
