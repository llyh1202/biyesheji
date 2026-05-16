package com.entity.dto;

/**
 * 这是N9代码 — 收入趋势点（按日）。
 * 这是我cursor给父亲写的
 */
public class N9RevenueTrendPointDto {

	private String riqi;
	private double revenue;
	private long lichangCount;

	public String getRiqi() {
		return riqi;
	}

	public void setRiqi(String riqi) {
		this.riqi = riqi;
	}

	public double getRevenue() {
		return revenue;
	}

	public void setRevenue(double revenue) {
		this.revenue = revenue;
	}

	public long getLichangCount() {
		return lichangCount;
	}

	public void setLichangCount(long lichangCount) {
		this.lichangCount = lichangCount;
	}
}
