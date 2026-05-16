package com.entity.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 这是M6代码 — 管理端首页扩展图表数据。
 * 这是我cursor给父亲写的
 */
public class M6HomeChartsDto {

	private double todayRevenue;
	private double weekRevenue;
	private double utilizationRate;
	private long cheweiTotal;
	private long cheweiOccupied;
	private List<N9RevenueTrendPointDto> revenueTrend = new ArrayList<N9RevenueTrendPointDto>();
	private List<M6TurnoverBarDto> turnoverTop = new ArrayList<M6TurnoverBarDto>();

	public double getTodayRevenue() {
		return todayRevenue;
	}

	public void setTodayRevenue(double todayRevenue) {
		this.todayRevenue = todayRevenue;
	}

	public double getWeekRevenue() {
		return weekRevenue;
	}

	public void setWeekRevenue(double weekRevenue) {
		this.weekRevenue = weekRevenue;
	}

	public double getUtilizationRate() {
		return utilizationRate;
	}

	public void setUtilizationRate(double utilizationRate) {
		this.utilizationRate = utilizationRate;
	}

	public long getCheweiTotal() {
		return cheweiTotal;
	}

	public void setCheweiTotal(long cheweiTotal) {
		this.cheweiTotal = cheweiTotal;
	}

	public long getCheweiOccupied() {
		return cheweiOccupied;
	}

	public void setCheweiOccupied(long cheweiOccupied) {
		this.cheweiOccupied = cheweiOccupied;
	}

	public List<N9RevenueTrendPointDto> getRevenueTrend() {
		return revenueTrend;
	}

	public void setRevenueTrend(List<N9RevenueTrendPointDto> revenueTrend) {
		this.revenueTrend = revenueTrend;
	}

	public List<M6TurnoverBarDto> getTurnoverTop() {
		return turnoverTop;
	}

	public void setTurnoverTop(List<M6TurnoverBarDto> turnoverTop) {
		this.turnoverTop = turnoverTop;
	}
}
