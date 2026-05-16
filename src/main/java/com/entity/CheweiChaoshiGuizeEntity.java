package com.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 这是N6代码 — 超时策略规则（预约保留时长 / 计费宽限期 / 未入场违约金）。
 * 这是我cursor给父亲写的
 */
@TableName("chewei_chaoshi_guize")
public class CheweiChaoshiGuizeEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Long id;
	private String guizeMingcheng;
	private String tingchechangmingcheng;
	private String quyu;
	private Integer yuyueBaoliuFenzhong;
	private Integer jifeiKuanxianFenzhong;
	private Double weiruchangKoufeiYuan;
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

	public Integer getYuyueBaoliuFenzhong() {
		return yuyueBaoliuFenzhong;
	}

	public void setYuyueBaoliuFenzhong(Integer yuyueBaoliuFenzhong) {
		this.yuyueBaoliuFenzhong = yuyueBaoliuFenzhong;
	}

	public Integer getJifeiKuanxianFenzhong() {
		return jifeiKuanxianFenzhong;
	}

	public void setJifeiKuanxianFenzhong(Integer jifeiKuanxianFenzhong) {
		this.jifeiKuanxianFenzhong = jifeiKuanxianFenzhong;
	}

	public Double getWeiruchangKoufeiYuan() {
		return weiruchangKoufeiYuan;
	}

	public void setWeiruchangKoufeiYuan(Double weiruchangKoufeiYuan) {
		this.weiruchangKoufeiYuan = weiruchangKoufeiYuan;
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
