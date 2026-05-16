package com.entity.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 这是N9代码 — 多维度统计报表结果。
 * 这是我cursor给父亲写的
 */
public class N9BaobiaoResultDto {

	private String kaishiRiqi;
	private String jieshuRiqi;
	private int statDays;
	private double totalRevenue;
	private List<N9DimensionStatRowDto> dimensions = new ArrayList<N9DimensionStatRowDto>();
	private List<N9RevenueTrendPointDto> revenueTrend = new ArrayList<N9RevenueTrendPointDto>();

	public String getKaishiRiqi() {
		return kaishiRiqi;
	}

	public void setKaishiRiqi(String kaishiRiqi) {
		this.kaishiRiqi = kaishiRiqi;
	}

	public String getJieshuRiqi() {
		return jieshuRiqi;
	}

	public void setJieshuRiqi(String jieshuRiqi) {
		this.jieshuRiqi = jieshuRiqi;
	}

	public int getStatDays() {
		return statDays;
	}

	public void setStatDays(int statDays) {
		this.statDays = statDays;
	}

	public double getTotalRevenue() {
		return totalRevenue;
	}

	public void setTotalRevenue(double totalRevenue) {
		this.totalRevenue = totalRevenue;
	}

	public List<N9DimensionStatRowDto> getDimensions() {
		return dimensions;
	}

	public void setDimensions(List<N9DimensionStatRowDto> dimensions) {
		this.dimensions = dimensions;
	}

	public List<N9RevenueTrendPointDto> getRevenueTrend() {
		return revenueTrend;
	}

	public void setRevenueTrend(List<N9RevenueTrendPointDto> revenueTrend) {
		this.revenueTrend = revenueTrend;
	}
}
