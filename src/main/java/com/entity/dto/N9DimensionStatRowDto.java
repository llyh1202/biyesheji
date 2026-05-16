package com.entity.dto;

/**
 * 这是N9代码 — 按车场/区域维度统计行。
 * 这是我cursor给父亲写的
 */
public class N9DimensionStatRowDto {

	private String tingchechangmingcheng;
	private String quyu;
	private long cheweiCount;
	private long ruchangCount;
	private long lichangCount;
	/** 日均车位周转次数 = 离场数 / 车位数 / 统计天数 */
	private double turnoverRate;
	/** 平均停车时长(小时) */
	private double avgParkingHours;
	private double revenue;

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

	public long getCheweiCount() {
		return cheweiCount;
	}

	public void setCheweiCount(long cheweiCount) {
		this.cheweiCount = cheweiCount;
	}

	public long getRuchangCount() {
		return ruchangCount;
	}

	public void setRuchangCount(long ruchangCount) {
		this.ruchangCount = ruchangCount;
	}

	public long getLichangCount() {
		return lichangCount;
	}

	public void setLichangCount(long lichangCount) {
		this.lichangCount = lichangCount;
	}

	public double getTurnoverRate() {
		return turnoverRate;
	}

	public void setTurnoverRate(double turnoverRate) {
		this.turnoverRate = turnoverRate;
	}

	public double getAvgParkingHours() {
		return avgParkingHours;
	}

	public void setAvgParkingHours(double avgParkingHours) {
		this.avgParkingHours = avgParkingHours;
	}

	public double getRevenue() {
		return revenue;
	}

	public void setRevenue(double revenue) {
		this.revenue = revenue;
	}
}
