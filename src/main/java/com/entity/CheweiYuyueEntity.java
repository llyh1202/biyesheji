package com.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 这是N4代码 — 车位时段预约实体。
 * 这是M1代码 — 扩展：预约支付态、流程节点、关联入场单/停车费单（与 N2/N3 流水一致）。
 * 这是我cursor给父亲写的
 */
@TableName("chewei_yuyue")
public class CheweiYuyueEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Long id;
	private Long cheweiId;
	private String tingchechangmingcheng;
	private String quyu;

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date kaishiShijian;

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date jieshuShijian;

	private String zhuangtai;
	/** 这是我cursor给父亲写的 — P1-05 预约用户账号（与登录 session username 一致，供「我的停车」查询） */
	private String yonghuzhanghao;
	/** 这是M1代码 — 预约链路支付状态（与 tingchejiaofei.ispay 区分又可对齐） */
	private String yuyueZhifuZhuangtai;
	/** 这是M1代码 — 当前业务节点：已预约待入场→已入场→已离场待支付→已完成 */
	private String liuchengJiedian;
	private Long chezijinchangId;
	private Long tingchejiaofeiId;

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date addtime;

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

	public String getZhuangtai() {
		return zhuangtai;
	}

	public void setZhuangtai(String zhuangtai) {
		this.zhuangtai = zhuangtai;
	}

	public String getYonghuzhanghao() {
		return yonghuzhanghao;
	}

	public void setYonghuzhanghao(String yonghuzhanghao) {
		this.yonghuzhanghao = yonghuzhanghao;
	}

	public String getYuyueZhifuZhuangtai() {
		return yuyueZhifuZhuangtai;
	}

	public void setYuyueZhifuZhuangtai(String yuyueZhifuZhuangtai) {
		this.yuyueZhifuZhuangtai = yuyueZhifuZhuangtai;
	}

	public String getLiuchengJiedian() {
		return liuchengJiedian;
	}

	public void setLiuchengJiedian(String liuchengJiedian) {
		this.liuchengJiedian = liuchengJiedian;
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

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
}
