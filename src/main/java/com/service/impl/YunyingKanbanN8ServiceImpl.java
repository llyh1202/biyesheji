package com.service.impl;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entity.dto.N8KanbanOverviewDto;
import com.entity.dto.N8PeriodKpiDto;
import com.service.YunyingKanbanN8Service;
import com.service.stat.CheweiTongjiReadM7Service;
import com.utils.R;

/**
 * 这是N8代码 — 今日/本周 KPI 聚合（预约、入场、离场、收入、利用率、超时）。
 * 这是M7代码 — 统计查询改由 CheweiTongjiReadM7Service 只读 SQL/视图承担。
 * 这是我cursor给父亲写的
 */
@Service("yunyingKanbanN8Service")
public class YunyingKanbanN8ServiceImpl implements YunyingKanbanN8Service {

	private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

	@Autowired
	private CheweiTongjiReadM7Service cheweiTongjiReadM7Service;

	@Override
	public R overview() {
		LocalDateTime now = LocalDateTime.now(ZONE);
		Date end = toDate(now);
		Date dayStart = toDate(now.toLocalDate().atStartOfDay());
		Date weekStart = toDate(now.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay());

		N8KanbanOverviewDto dto = new N8KanbanOverviewDto();
		dto.setSnapshotAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(end));
		dto.setToday(buildPeriodKpi(dayStart, end));
		dto.setWeek(buildPeriodKpi(weekStart, end));
		fillCheweiSnapshot(dto);
		return R.ok().put("data", dto);
	}

	private N8PeriodKpiDto buildPeriodKpi(Date start, Date end) {
		N8PeriodKpiDto k = new N8PeriodKpiDto();
		k.setYuyueCount(countYuyue(start, end));
		k.setRuchangCount(countRuchang(start, end));
		k.setLichangCount(countLichang(start, end));
		double parkingRev = sumParkingRevenue(start, end);
		double bujiaoRev = sumBujiaoRevenue(start, end);
		k.setParkingRevenue(round2(parkingRev));
		k.setBujiaoRevenue(round2(bujiaoRev));
		k.setRevenue(round2(parkingRev + bujiaoRev));
		long cancel = countChaoshiCancel(start, end);
		long bujiao = countChaoshiBujiao(start, end);
		k.setChaoshiCancelCount(cancel);
		k.setChaoshiBujiaoCount(bujiao);
		k.setChaoshiTotal(cancel + bujiao);
		return k;
	}

	private long countYuyue(Date start, Date end) {
		return cheweiTongjiReadM7Service.countYuyue(start, end);
	}

	private long countRuchang(Date start, Date end) {
		return cheweiTongjiReadM7Service.countRuchang(start, end);
	}

	private long countLichang(Date start, Date end) {
		return cheweiTongjiReadM7Service.countLichang(start, end);
	}

	private double sumParkingRevenue(Date start, Date end) {
		return cheweiTongjiReadM7Service.sumParkingRevenue(start, end);
	}

	private double sumBujiaoRevenue(Date start, Date end) {
		return cheweiTongjiReadM7Service.sumBujiaoRevenue(start, end);
	}

	private long countChaoshiCancel(Date start, Date end) {
		return cheweiTongjiReadM7Service.countYuyueCancel(start, end);
	}

	private long countChaoshiBujiao(Date start, Date end) {
		return cheweiTongjiReadM7Service.countChaoshiBujiao(start, end);
	}

	private void fillCheweiSnapshot(N8KanbanOverviewDto dto) {
		Map<String, Object> snap = cheweiTongjiReadM7Service.cheweiGlobalSnapshot();
		long total = num(snap, "chewei_total");
		long free = num(snap, "chewei_free");
		long occupied = Math.max(0, total - free);
		dto.setCheweiTotal(total);
		dto.setCheweiOccupied(occupied);
		dto.setUtilizationRate(total <= 0 ? 0.0 : round2(occupied * 100.0 / total));
	}

	private static long num(Map<String, Object> m, String key) {
		if (m == null || m.isEmpty()) {
			return 0L;
		}
		Object v = m.get(key);
		if (v == null) {
			v = m.get(key.toUpperCase());
		}
		return v == null ? 0L : ((Number) v).longValue();
	}

	private static Date toDate(LocalDateTime ldt) {
		return Date.from(ldt.atZone(ZONE).toInstant());
	}

	private static double round2(double v) {
		return Math.round(v * 100.0) / 100.0;
	}
}
