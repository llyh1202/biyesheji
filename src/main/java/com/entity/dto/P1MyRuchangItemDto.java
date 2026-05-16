package com.entity.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 这是我cursor给父亲写的 — P1-08 我的停车汇总：在场入场单项
 */
public class P1MyRuchangItemDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private Long cheweiId;
	private String cheweibianhao;
	private String tingchechangmingcheng;
	private String quyu;
	private String chepaihao;
	private String xingming;

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date jinchangshijian;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCheweiId() {
		return cheweiId;
	}

	public void setCheweiId(Long cheweiId) {
		this.cheweiId = cheweiId;
	}

	public String getCheweibianhao() {
		return cheweibianhao;
	}

	public void setCheweibianhao(String cheweibianhao) {
		this.cheweibianhao = cheweibianhao;
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

	public String getXingming() {
		return xingming;
	}

	public void setXingming(String xingming) {
		this.xingming = xingming;
	}

	public Date getJinchangshijian() {
		return jinchangshijian;
	}

	public void setJinchangshijian(Date jinchangshijian) {
		this.jinchangshijian = jinchangshijian;
	}
}
