package com.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entity.dto.M6HomeChartsDto;
import com.entity.dto.M6TurnoverBarDto;
import com.entity.dto.N8KanbanOverviewDto;
import com.entity.dto.N8PeriodKpiDto;
import com.entity.dto.N9BaobiaoQueryDto;
import com.entity.dto.N9BaobiaoResultDto;
import com.entity.dto.N9DimensionStatRowDto;
import com.entity.dto.N9RevenueTrendPointDto;
import com.service.YunyingBaobiaoN9Service;
import com.service.YunyingHomeM6Service;
import com.service.YunyingKanbanN8Service;
import com.utils.R;

/**
 * 这是M6代码 — 聚合 N8/N9 数据供首页收入与周转图表展示。
 * 这是我cursor给父亲写的
 */
@Service("yunyingHomeM6Service")
public class YunyingHomeM6ServiceImpl implements YunyingHomeM6Service {

	private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final int TOP_TURNOVER = 6;

	@Autowired
	private YunyingKanbanN8Service yunyingKanbanN8Service;
	@Autowired
	private YunyingBaobiaoN9Service yunyingBaobiaoN9Service;

	@Override
	public R homeCharts() {
		M6HomeChartsDto dto = new M6HomeChartsDto();
		R n8 = yunyingKanbanN8Service.overview();
		if (n8 != null && Integer.valueOf(0).equals(n8.get("code")) && n8.get("data") instanceof N8KanbanOverviewDto) {
			N8KanbanOverviewDto k = (N8KanbanOverviewDto) n8.get("data");
			dto.setUtilizationRate(k.getUtilizationRate());
			dto.setCheweiTotal(k.getCheweiTotal());
			dto.setCheweiOccupied(k.getCheweiOccupied());
			N8PeriodKpiDto today = k.getToday();
			N8PeriodKpiDto week = k.getWeek();
			if (today != null) {
				dto.setTodayRevenue(today.getRevenue());
			}
			if (week != null) {
				dto.setWeekRevenue(week.getRevenue());
			}
		}
		LocalDate end = LocalDate.now();
		LocalDate start = end.minusDays(6);
		N9BaobiaoQueryDto q = new N9BaobiaoQueryDto();
		q.setKaishiRiqi(start.format(DAY_FMT));
		q.setJieshuRiqi(end.format(DAY_FMT));
		R n9 = yunyingBaobiaoN9Service.query(q);
		if (n9 != null && Integer.valueOf(0).equals(n9.get("code")) && n9.get("data") instanceof N9BaobiaoResultDto) {
			N9BaobiaoResultDto b = (N9BaobiaoResultDto) n9.get("data");
			if (b.getRevenueTrend() != null) {
				dto.setRevenueTrend(b.getRevenueTrend());
			}
			dto.setTurnoverTop(buildTurnoverTop(b.getDimensions()));
		}
		return R.ok().put("data", dto);
	}

	private List<M6TurnoverBarDto> buildTurnoverTop(List<N9DimensionStatRowDto> dims) {
		if (dims == null || dims.isEmpty()) {
			return new ArrayList<M6TurnoverBarDto>();
		}
		List<N9DimensionStatRowDto> copy = new ArrayList<N9DimensionStatRowDto>(dims);
		Collections.sort(copy, new Comparator<N9DimensionStatRowDto>() {
			@Override
			public int compare(N9DimensionStatRowDto a, N9DimensionStatRowDto b) {
				return Double.compare(b.getTurnoverRate(), a.getTurnoverRate());
			}
		});
		List<M6TurnoverBarDto> bars = new ArrayList<M6TurnoverBarDto>();
		int n = Math.min(TOP_TURNOVER, copy.size());
		for (int i = 0; i < n; i++) {
			N9DimensionStatRowDto d = copy.get(i);
			M6TurnoverBarDto bar = new M6TurnoverBarDto();
			String lot = StringUtils.defaultString(d.getTingchechangmingcheng(), "未命名");
			String quyu = StringUtils.defaultString(d.getQuyu(), "");
			bar.setLabel(quyu.length() > 0 ? lot + "/" + quyu : lot);
			bar.setTurnoverRate(d.getTurnoverRate());
			bar.setRevenue(d.getRevenue());
			bars.add(bar);
		}
		return bars;
	}
}
