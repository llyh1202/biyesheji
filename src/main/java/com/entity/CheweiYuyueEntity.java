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

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
}
