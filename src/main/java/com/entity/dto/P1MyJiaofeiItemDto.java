package com.entity.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 这是我cursor给父亲写的 — P1-08 我的停车汇总：待支付缴费单项
 */
public class P1MyJiaofeiItemDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String dingdanhao;
	private Long cheweiId;
	private Long chezijinchangId;
	private String tingchechangmingcheng;
	private String quyu;
	private String chepaihao;

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date jinchangshijian;

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lichangshijian;

	private Double bencitingcheshizhang;
	private Double bencitingchefeiyong;
	private String ispay;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDingdanhao() {
		return dingdanhao;
	}

	public void setDingdanhao(String dingdanhao) {
		this.dingdanhao = dingdanhao;
	}

	public Long getCheweiId() {
		return cheweiId;
	}

	public void setCheweiId(Long cheweiId) {
		this.cheweiId = cheweiId;
	}

	public Long getChezijinchangId() {
		return chezijinchangId;
	}

	public void setChezijinchangId(Long chezijinchangId) {
		this.chezijinchangId = chezijinchangId;
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

	public String getChepaihao() {
		return chepaihao;
	}

	public void setChepaihao(String chepaihao) {
		this.chepaihao = chepaihao;
	}

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

	public Double getBencitingcheshizhang() {
		return bencitingcheshizhang;
	}

	public void setBencitingcheshizhang(Double bencitingcheshizhang) {
		this.bencitingcheshizhang = bencitingcheshizhang;
	}

	public Double getBencitingchefeiyong() {
		return bencitingchefeiyong;
	}

	public void setBencitingchefeiyong(Double bencitingchefeiyong) {
		this.bencitingchefeiyong = bencitingchefeiyong;
	}

	public String getIspay() {
		return ispay;
	}

	public void setIspay(String ispay) {
		this.ispay = ispay;
	}
}
