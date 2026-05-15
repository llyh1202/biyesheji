package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.lang.reflect.InvocationTargetException;
import java.io.Serializable;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * 车位编号主数据（车场/区域下单车位）。N1 实体表 chewei。
 * 这是我cursor给父亲写的
 */
@TableName("chewei")
public class CheweiEntity<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	public CheweiEntity() {
	}

	public CheweiEntity(T t) {
		try {
			BeanUtils.copyProperties(this, t);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@TableId(type = IdType.AUTO)
	private Long id;

	private String tingchechangmingcheng;
	private String quyu;
	private String cheweibianhao;
	private String zhuangtai;
	private Long cheweixinxiId;
	/** N2：当前入场单 id */
	private Long chezijinchangId;
	/** N2：当前离场/缴费订单 id */
	private Long tingchejiaofeiId;
	private String beizhu;

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	private Date addtime;

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

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
}
