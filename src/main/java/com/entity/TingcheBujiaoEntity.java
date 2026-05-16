package com.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 这是N7代码 — 续费/超时补缴单。
 * 这是我cursor给父亲写的
 */
@TableName("tingche_bujiao")
public class TingcheBujiaoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Long id;
	private String danhao;
	private String leixing;
	private Long chezijinchangId;
	private Long tingchejiaofeiId;
	private String yonghuzhanghao;
	private String xingming;
	private String chepaihao;
	private Double jine;
	private String yuanyin;
	private String zhuangtai;
	private String guanliyuanZhanghao;
	private String beizhu;

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date zhifuShijian;

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date addtime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDanhao() {
		return danhao;
	}

	public void setDanhao(String danhao) {
		this.danhao = danhao;
	}

	public String getLeixing() {
		return leixing;
	}

	public void setLeixing(String leixing) {
		this.leixing = leixing;
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

	public String getChepaihao() {
		return chepaihao;
	}

	public void setChepaihao(String chepaihao) {
		this.chepaihao = chepaihao;
	}

	public Double getJine() {
		return jine;
	}

	public void setJine(Double jine) {
		this.jine = jine;
	}

	public String getYuanyin() {
		return yuanyin;
	}

	public void setYuanyin(String yuanyin) {
		this.yuanyin = yuanyin;
	}

	public String getZhuangtai() {
		return zhuangtai;
	}

	public void setZhuangtai(String zhuangtai) {
		this.zhuangtai = zhuangtai;
	}

	public String getGuanliyuanZhanghao() {
		return guanliyuanZhanghao;
	}

	public void setGuanliyuanZhanghao(String guanliyuanZhanghao) {
		this.guanliyuanZhanghao = guanliyuanZhanghao;
	}

	public String getBeizhu() {
		return beizhu;
	}

	public void setBeizhu(String beizhu) {
		this.beizhu = beizhu;
	}

	public Date getZhifuShijian() {
		return zhifuShijian;
	}

	public void setZhifuShijian(Date zhifuShijian) {
		this.zhifuShijian = zhifuShijian;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
}
