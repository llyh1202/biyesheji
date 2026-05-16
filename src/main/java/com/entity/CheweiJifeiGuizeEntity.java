package com.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 这是M5代码 — 停车计费规则（首小时/阶梯/封顶）。
 * 这是我cursor给父亲写的
 */
@TableName("chewei_jifei_guize")
public class CheweiJifeiGuizeEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Long id;
	private String guizeMingcheng;
	private String tingchechangmingcheng;
	private String quyu;
	private String jifeiMoshi;
	private Integer meixiaoshiDanjia;
	private Double shouxiaoshiYuan;
	private Integer jietiDanjia;
	private Double fengdingYuan;
	private Double zuixiaoJifeiXiaoshi;
	private String qiyong;
	private String beizhu;

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date addtime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGuizeMingcheng() {
		return guizeMingcheng;
	}

	public void setGuizeMingcheng(String guizeMingcheng) {
		this.guizeMingcheng = guizeMingcheng;
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

	public String getJifeiMoshi() {
		return jifeiMoshi;
	}

	public void setJifeiMoshi(String jifeiMoshi) {
		this.jifeiMoshi = jifeiMoshi;
	}

	public Integer getMeixiaoshiDanjia() {
		return meixiaoshiDanjia;
	}

	public void setMeixiaoshiDanjia(Integer meixiaoshiDanjia) {
		this.meixiaoshiDanjia = meixiaoshiDanjia;
	}

	public Double getShouxiaoshiYuan() {
		return shouxiaoshiYuan;
	}

	public void setShouxiaoshiYuan(Double shouxiaoshiYuan) {
		this.shouxiaoshiYuan = shouxiaoshiYuan;
	}

	public Integer getJietiDanjia() {
		return jietiDanjia;
	}

	public void setJietiDanjia(Integer jietiDanjia) {
		this.jietiDanjia = jietiDanjia;
	}

	public Double getFengdingYuan() {
		return fengdingYuan;
	}

	public void setFengdingYuan(Double fengdingYuan) {
		this.fengdingYuan = fengdingYuan;
	}

	public Double getZuixiaoJifeiXiaoshi() {
		return zuixiaoJifeiXiaoshi;
	}

	public void setZuixiaoJifeiXiaoshi(Double zuixiaoJifeiXiaoshi) {
		this.zuixiaoJifeiXiaoshi = zuixiaoJifeiXiaoshi;
	}

	public String getQiyong() {
		return qiyong;
	}

	public void setQiyong(String qiyong) {
		this.qiyong = qiyong;
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
