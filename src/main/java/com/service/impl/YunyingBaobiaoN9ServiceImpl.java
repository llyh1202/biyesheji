package com.service.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entity.dto.N9BaobiaoQueryDto;
import com.entity.dto.N9BaobiaoResultDto;
import com.entity.dto.N9DimensionStatRowDto;
import com.entity.dto.N9RevenueTrendPointDto;
import com.service.YunyingBaobiaoN9Service;
import com.service.stat.CheweiTongjiReadM7Service;
import com.utils.R;

/**
 * 这是N9代码 — 按车场/区域/时段：周转率、平均停车时长、收入趋势；支持 Excel 导出。
 * 这是M7代码 — 聚合改由只读 SQL/视图（CheweiTongjiReadM7Service）完成。
 * 这是我cursor给父亲写的
 */
@Service("yunyingBaobiaoN9Service")
public class YunyingBaobiaoN9ServiceImpl implements YunyingBaobiaoN9Service {

	private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");
	private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Autowired
	private CheweiTongjiReadM7Service cheweiTongjiReadM7Service;

	private static class DimAgg {
		String lot;
		String quyu;
		long cheweiCount;
		long ruchang;
		long lichang;
		double revenue;
		double durationSum;
		long durationCnt;
	}

	@Override
	public R query(N9BaobiaoQueryDto query) {
		try {
			N9BaobiaoResultDto result = buildReport(query);
			return R.ok().put("data", result);
		} catch (IllegalArgumentException ex) {
			return R.error(ex.getMessage());
		}
	}

	@Override
	public void exportExcel(N9BaobiaoQueryDto query, HttpServletResponse response) throws IOException {
		N9BaobiaoResultDto result = buildReport(query);
		try (XSSFWorkbook wb = new XSSFWorkbook()) {
			Sheet s1 = wb.createSheet("维度汇总");
			Row h1 = s1.createRow(0);
			String[] cols = { "停车场", "区域", "车位数", "入场量", "离场量", "周转率(次/位/日)", "平均停车时长(小时)", "收入(元)" };
			for (int i = 0; i < cols.length; i++) {
				h1.createCell(i).setCellValue(cols[i]);
			}
			int r = 1;
			for (N9DimensionStatRowDto row : result.getDimensions()) {
				Row dr = s1.createRow(r++);
				dr.createCell(0).setCellValue(nz(row.getTingchechangmingcheng()));
				dr.createCell(1).setCellValue(nz(row.getQuyu()));
				dr.createCell(2).setCellValue(row.getCheweiCount());
				dr.createCell(3).setCellValue(row.getRuchangCount());
				dr.createCell(4).setCellValue(row.getLichangCount());
				dr.createCell(5).setCellValue(row.getTurnoverRate());
				dr.createCell(6).setCellValue(row.getAvgParkingHours());
				dr.createCell(7).setCellValue(row.getRevenue());
			}
			Sheet s2 = wb.createSheet("收入趋势");
			Row h2 = s2.createRow(0);
			h2.createCell(0).setCellValue("日期");
			h2.createCell(1).setCellValue("收入(元)");
			h2.createCell(2).setCellValue("离场笔数");
			int r2 = 1;
			for (N9RevenueTrendPointDto p : result.getRevenueTrend()) {
				Row dr = s2.createRow(r2++);
				dr.createCell(0).setCellValue(p.getRiqi());
				dr.createCell(1).setCellValue(p.getRevenue());
				dr.createCell(2).setCellValue(p.getLichangCount());
			}
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition",
					"attachment;filename=n9_baobiao_" + result.getKaishiRiqi() + "_" + result.getJieshuRiqi() + ".xlsx");
			wb.write(response.getOutputStream());
		}
	}

	private N9BaobiaoResultDto buildReport(N9BaobiaoQueryDto query) {
		LocalDate end = parseDate(query != null ? query.getJieshuRiqi() : null, LocalDate.now(ZONE));
		LocalDate start = parseDate(query != null ? query.getKaishiRiqi() : null, end.minusDays(6));
		if (start.isAfter(end)) {
			throw new IllegalArgumentException("开始日期不能晚于结束日期");
		}
		int days = (int) ChronoUnit.DAYS.between(start, end) + 1;
		Date startDt = toDateStart(start);
		Date endDt = toDateEnd(end);
		String lotF = query != null ? nz(query.getTingchechangmingcheng()) : "";
		String quyuF = query != null ? nz(query.getQuyu()) : "";

		Map<String, DimAgg> dimMap = initDimMap(lotF, quyuF);
		Map<String, Double> trendRev = new LinkedHashMap<String, Double>();
		Map<String, Long> trendLichang = new LinkedHashMap<String, Long>();
		for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
			String key = d.format(DAY_FMT);
			trendRev.put(key, 0.0);
			trendLichang.put(key, 0L);
		}

		aggregateRuchang(dimMap, startDt, endDt, lotF, quyuF);
		double parkingRev = aggregateLichang(dimMap, trendRev, trendLichang, startDt, endDt, lotF, quyuF);
		double bujiaoRev = mergeBujiaoTrend(trendRev, startDt, endDt, lotF, quyuF);
		double totalRev = round2(parkingRev + bujiaoRev);

		List<N9DimensionStatRowDto> rows = new ArrayList<N9DimensionStatRowDto>();
		for (DimAgg a : dimMap.values()) {
			N9DimensionStatRowDto row = new N9DimensionStatRowDto();
			row.setTingchechangmingcheng(a.lot);
			row.setQuyu(a.quyu);
			row.setCheweiCount(a.cheweiCount);
			row.setRuchangCount(a.ruchang);
			row.setLichangCount(a.lichang);
			double turnover = a.cheweiCount <= 0 ? 0.0 : (double) a.lichang / a.cheweiCount / days;
			row.setTurnoverRate(round2(turnover));
			row.setAvgParkingHours(a.durationCnt <= 0 ? 0.0 : round2(a.durationSum / a.durationCnt));
			row.setRevenue(round2(a.revenue));
			rows.add(row);
		}
		Collections.sort(rows, new Comparator<N9DimensionStatRowDto>() {
			@Override
			public int compare(N9DimensionStatRowDto a, N9DimensionStatRowDto b) {
				return Double.compare(b.getRevenue(), a.getRevenue());
			}
		});

		List<N9RevenueTrendPointDto> trend = new ArrayList<N9RevenueTrendPointDto>();
		for (Map.Entry<String, Double> e : trendRev.entrySet()) {
			N9RevenueTrendPointDto p = new N9RevenueTrendPointDto();
			p.setRiqi(e.getKey());
			p.setRevenue(round2(e.getValue()));
			p.setLichangCount(trendLichang.get(e.getKey()) != null ? trendLichang.get(e.getKey()) : 0L);
			trend.add(p);
		}

		N9BaobiaoResultDto result = new N9BaobiaoResultDto();
		result.setKaishiRiqi(start.format(DAY_FMT));
		result.setJieshuRiqi(end.format(DAY_FMT));
		result.setStatDays(days);
		result.setTotalRevenue(totalRev);
		result.setDimensions(rows);
		result.setRevenueTrend(trend);
		return result;
	}

	private Map<String, DimAgg> initDimMap(String lotF, String quyuF) {
		String lotParam = StringUtils.isNotBlank(lotF) ? lotF : null;
		String quyuParam = StringUtils.isNotBlank(quyuF) ? quyuF : null;
		List<Map<String, Object>> list = cheweiTongjiReadM7Service.listDimChewei(lotParam, quyuParam);
		Map<String, DimAgg> map = new HashMap<String, DimAgg>(32);
		for (Map<String, Object> row : list) {
			String lot = nz(str(row, "tingchechangmingcheng"));
			String quyu = nz(str(row, "quyu"));
			DimAgg a = new DimAgg();
			a.lot = lot;
			a.quyu = quyu;
			a.cheweiCount = num(row, "chewei_count");
			map.put(dimKey(lot, quyu), a);
		}
		if (map.isEmpty()) {
			String lot = StringUtils.isNotBlank(lotF) ? lotF : "全部";
			String quyu = StringUtils.isNotBlank(quyuF) ? quyuF : "";
			DimAgg a = new DimAgg();
			a.lot = lot;
			a.quyu = quyu;
			a.cheweiCount = 0;
			map.put(dimKey(lot, quyu), a);
		}
		return map;
	}

	private void aggregateRuchang(Map<String, DimAgg> dimMap, Date start, Date end, String lotF, String quyuF) {
		String lotParam = StringUtils.isNotBlank(lotF) ? lotF : null;
		String quyuParam = StringUtils.isNotBlank(quyuF) ? quyuF : null;
		for (Map<String, Object> row : cheweiTongjiReadM7Service.listRuchangByDim(start, end, lotParam, quyuParam)) {
			DimAgg a = ensureDim(dimMap, str(row, "tingchechangmingcheng"), str(row, "quyu"));
			a.ruchang += num(row, "ruchang_count");
		}
	}

	private double aggregateLichang(Map<String, DimAgg> dimMap, Map<String, Double> trendRev, Map<String, Long> trendLichang,
			Date start, Date end, String lotF, String quyuF) {
		String lotParam = StringUtils.isNotBlank(lotF) ? lotF : null;
		String quyuParam = StringUtils.isNotBlank(quyuF) ? quyuF : null;
		double sumPaid = 0.0;
		for (Map<String, Object> row : cheweiTongjiReadM7Service.listRevenueDim(start, end, lotParam, quyuParam)) {
			DimAgg a = ensureDim(dimMap, str(row, "tingchechangmingcheng"), str(row, "quyu"));
			long lc = num(row, "lichang_count");
			double rev = dbl(row, "parking_revenue");
			a.lichang += lc;
			a.revenue += rev;
			a.durationSum += dbl(row, "duration_sum_hours");
			a.durationCnt += num(row, "duration_cnt");
			sumPaid += rev;
		}
		for (Map<String, Object> row : cheweiTongjiReadM7Service.listRevenueDaily(start, end, lotParam, quyuParam)) {
			String day = formatDayKey(row.get("stat_date"));
			if (trendLichang.containsKey(day)) {
				trendLichang.put(day, num(row, "lichang_count"));
			}
			if (trendRev.containsKey(day)) {
				trendRev.put(day, dbl(row, "parking_revenue"));
			}
		}
		return sumPaid;
	}

	private double mergeBujiaoTrend(Map<String, Double> trendRev, Date start, Date end, String lotF, String quyuF) {
		String lotParam = StringUtils.isNotBlank(lotF) ? lotF : null;
		String quyuParam = StringUtils.isNotBlank(quyuF) ? quyuF : null;
		double sum = 0.0;
		for (Map<String, Object> row : cheweiTongjiReadM7Service.listBujiaoDaily(start, end, lotParam, quyuParam)) {
			double fee = dbl(row, "bujiao_revenue");
			sum += fee;
			String day = formatDayKey(row.get("stat_date"));
			if (trendRev.containsKey(day)) {
				trendRev.put(day, trendRev.get(day) + fee);
			}
		}
		return sum;
	}

	private static DimAgg ensureDim(Map<String, DimAgg> dimMap, String lot, String quyu) {
		String l = StringUtils.isBlank(lot) ? "未命名车场" : lot.trim();
		String q = quyu == null ? "" : quyu.trim();
		String key = dimKey(l, q);
		DimAgg a = dimMap.get(key);
		if (a == null) {
			a = new DimAgg();
			a.lot = l;
			a.quyu = q;
			dimMap.put(key, a);
		}
		return a;
	}

	private static String dimKey(String lot, String quyu) {
		return lot + "\u0001" + (quyu == null ? "" : quyu);
	}

	private static String formatDay(Date d) {
		if (d == null) {
			return "";
		}
		return d.toInstant().atZone(ZONE).toLocalDate().format(DAY_FMT);
	}

	private static String formatDayKey(Object statDate) {
		if (statDate == null) {
			return "";
		}
		if (statDate instanceof java.sql.Date) {
			return ((java.sql.Date) statDate).toLocalDate().format(DAY_FMT);
		}
		if (statDate instanceof Date) {
			return formatDay((Date) statDate);
		}
		return statDate.toString().substring(0, Math.min(10, statDate.toString().length()));
	}

	private static String str(Map<String, Object> m, String key) {
		Object v = m.get(key);
		if (v == null) {
			v = m.get(key.toUpperCase());
		}
		return v == null ? "" : v.toString();
	}

	private static long num(Map<String, Object> m, String key) {
		Object v = m.get(key);
		if (v == null) {
			v = m.get(key.toUpperCase());
		}
		return v == null ? 0L : ((Number) v).longValue();
	}

	private static double dbl(Map<String, Object> m, String key) {
		Object v = m.get(key);
		if (v == null) {
			v = m.get(key.toUpperCase());
		}
		return v == null ? 0.0 : ((Number) v).doubleValue();
	}

	private static LocalDate parseDate(String s, LocalDate defaultVal) {
		if (StringUtils.isBlank(s)) {
			return defaultVal;
		}
		return LocalDate.parse(s.trim(), DAY_FMT);
	}

	private static Date toDateStart(LocalDate d) {
		return Date.from(d.atStartOfDay(ZONE).toInstant());
	}

	private static Date toDateEnd(LocalDate d) {
		return Date.from(d.plusDays(1).atStartOfDay(ZONE).toInstant().minusMillis(1));
	}

	private static String nz(String s) {
		return s == null ? "" : s.trim();
	}

	private static double round2(double v) {
		return Math.round(v * 100.0) / 100.0;
	}
}
