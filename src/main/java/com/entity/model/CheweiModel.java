package com.entity.model;

import java.io.Serializable;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 车位主数据 接收参数。N1。
 * 这是我cursor给父亲写的
 */
public class CheweiModel implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String tingchechangmingcheng;
	private String quyu;
	private String cheweibianhao;
	private String zhuangtai;
	private Long cheweixinxiId;
	private Long chezijinchangId;
	private Long tingchejiaofeiId;
	private String beizhu;
	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	private Date addtime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Long getCheweixinxiId() {
		return cheweixinxiId;
	}

	public void setCheweixinxiId(Long cheweixinxiId) {
		this.cheweixinxiId = cheweixinxiId;
	}

	public Long getChezijinchangId() {
		return chezijinchangId;
	}

	public void setChezijinchangId(Long chezijinchangId) {
		this.chezijinchangId = chezijinchangId;
	}

	public Long getTingchejiaofeiId() {
		return tingchejiaofeiId;
	}

	public void setTingchejiaofeiId(Long tingchejiaofeiId) {
		this.tingchejiaofeiId = tingchejiaofeiId;
	}

	public String getBeizhu() {
		return beizhu;
	}

	public void setBeizhu(String beizhu) {
		this.beizhu = beizhu;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
}
