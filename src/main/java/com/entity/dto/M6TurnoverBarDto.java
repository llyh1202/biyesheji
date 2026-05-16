package com.entity.dto;

/**
 * 这是M6代码 — 首页周转率柱状图项。
 * 这是我cursor给父亲写的
 */
public class M6TurnoverBarDto {

	private String label;
	private double turnoverRate;
	private double revenue;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public double getTurnoverRate() {
		return turnoverRate;
	}

	public void setTurnoverRate(double turnoverRate) {
		this.turnoverRate = turnoverRate;
	}

	public double getRevenue() {
		return revenue;
	}

	public void setRevenue(double revenue) {
		this.revenue = revenue;
	}
}
