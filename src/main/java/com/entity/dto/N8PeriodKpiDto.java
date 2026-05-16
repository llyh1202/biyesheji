package com.entity.dto;

/**
 * 这是N8代码 — 运营看板单周期 KPI。
 * 这是我cursor给父亲写的
 */
public class N8PeriodKpiDto {

	private long yuyueCount;
	private long ruchangCount;
	private long lichangCount;
	private double revenue;
	private double parkingRevenue;
	private double bujiaoRevenue;
	private long chaoshiCancelCount;
	private long chaoshiBujiaoCount;
	private long chaoshiTotal;

	public long getYuyueCount() {
		return yuyueCount;
	}

	public void setYuyueCount(long yuyueCount) {
		this.yuyueCount = yuyueCount;
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

	public double getRevenue() {
		return revenue;
	}

	public void setRevenue(double revenue) {
		this.revenue = revenue;
	}

	public double getParkingRevenue() {
		return parkingRevenue;
	}

	public void setParkingRevenue(double parkingRevenue) {
		this.parkingRevenue = parkingRevenue;
	}

	public double getBujiaoRevenue() {
		return bujiaoRevenue;
	}

	public void setBujiaoRevenue(double bujiaoRevenue) {
		this.bujiaoRevenue = bujiaoRevenue;
	}

	public long getChaoshiCancelCount() {
		return chaoshiCancelCount;
	}

	public void setChaoshiCancelCount(long chaoshiCancelCount) {
		this.chaoshiCancelCount = chaoshiCancelCount;
	}

	public long getChaoshiBujiaoCount() {
		return chaoshiBujiaoCount;
	}

	public void setChaoshiBujiaoCount(long chaoshiBujiaoCount) {
		this.chaoshiBujiaoCount = chaoshiBujiaoCount;
	}

	public long getChaoshiTotal() {
		return chaoshiTotal;
	}

	public void setChaoshiTotal(long chaoshiTotal) {
		this.chaoshiTotal = chaoshiTotal;
	}
}
