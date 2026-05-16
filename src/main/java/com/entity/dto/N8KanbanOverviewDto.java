package com.entity.dto;

/**
 * 这是N8代码 — 运营总览看板响应。
 * 这是我cursor给父亲写的
 */
public class N8KanbanOverviewDto {

	private String snapshotAt;
	private N8PeriodKpiDto today;
	private N8PeriodKpiDto week;
	/** 当前车位利用率（时点快照，非周期累计） */
	private long cheweiTotal;
	private long cheweiOccupied;
	private double utilizationRate;

	public String getSnapshotAt() {
		return snapshotAt;
	}

	public void setSnapshotAt(String snapshotAt) {
		this.snapshotAt = snapshotAt;
	}

	public N8PeriodKpiDto getToday() {
		return today;
	}

	public void setToday(N8PeriodKpiDto today) {
		this.today = today;
	}

	public N8PeriodKpiDto getWeek() {
		return week;
	}

	public void setWeek(N8PeriodKpiDto week) {
		this.week = week;
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

	public double getUtilizationRate() {
		return utilizationRate;
	}

	public void setUtilizationRate(double utilizationRate) {
		this.utilizationRate = utilizationRate;
	}
}
