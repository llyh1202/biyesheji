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

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.dao.TingcheBujiaoDao;
import com.entity.CheweiEntity;
import com.entity.ChezijinchangEntity;
import com.entity.TingcheBujiaoEntity;
import com.entity.TingchejiaofeiEntity;
import com.entity.dto.N9BaobiaoQueryDto;
import com.entity.dto.N9BaobiaoResultDto;
import com.entity.dto.N9DimensionStatRowDto;
import com.entity.dto.N9RevenueTrendPointDto;
import com.constant.TingcheBujiaoZhuangtaiN7;
import com.service.CheweiService;
import com.service.ChezijinchangService;
import com.service.TingchejiaofeiService;
import com.service.YunyingBaobiaoN9Service;
import com.utils.R;

/**
 * 这是N9代码 — 按车场/区域/时段：周转率、平均停车时长、收入趋势；支持 Excel 导出。
 * 这是我cursor给父亲写的
 */
@Service("yunyingBaobiaoN9Service")
public class YunyingBaobiaoN9ServiceImpl implements YunyingBaobiaoN9Service {

	private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");
	private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Autowired
	private CheweiService cheweiService;
	@Autowired
	private ChezijinchangService chezijinchangService;
	@Autowired
	private TingchejiaofeiService tingchejiaofeiService;
	@Autowired
	private TingcheBujiaoDao tingcheBujiaoDao;

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
		double bujiaoRev = aggregateBujiao(trendRev, startDt, endDt, lotF, quyuF);
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
		EntityWrapper<CheweiEntity> w = new EntityWrapper<CheweiEntity>();
		if (StringUtils.isNotBlank(lotF)) {
			w.eq("tingchechangmingcheng", lotF);
		}
		if (StringUtils.isNotBlank(quyuF)) {
			w.eq("quyu", quyuF);
		}
		List<CheweiEntity> list = cheweiService.selectList(w);
		Map<String, DimAgg> map = new HashMap<String, DimAgg>(32);
		for (CheweiEntity c : list) {
			String lot = nz(c.getTingchechangmingcheng());
			String quyu = nz(c.getQuyu());
			if (StringUtils.isBlank(lot)) {
				lot = "未命名车场";
			}
			DimAgg a = map.get(dimKey(lot, quyu));
			if (a == null) {
				a = new DimAgg();
				a.lot = lot;
				a.quyu = quyu;
				map.put(dimKey(lot, quyu), a);
			}
			a.cheweiCount++;
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
		EntityWrapper<ChezijinchangEntity> w = new EntityWrapper<ChezijinchangEntity>();
		w.ge("jinchangshijian", start);
		w.le("jinchangshijian", end);
		if (StringUtils.isNotBlank(lotF)) {
			w.eq("tingchechangmingcheng", lotF);
		}
		if (StringUtils.isNotBlank(quyuF)) {
			w.eq("quyu", quyuF);
		}
		List<ChezijinchangEntity> list = chezijinchangService.selectList(w);
		for (ChezijinchangEntity e : list) {
			DimAgg a = ensureDim(dimMap, e.getTingchechangmingcheng(), e.getQuyu());
			a.ruchang++;
		}
	}

	private double aggregateLichang(Map<String, DimAgg> dimMap, Map<String, Double> trendRev, Map<String, Long> trendLichang,
			Date start, Date end, String lotF, String quyuF) {
		EntityWrapper<TingchejiaofeiEntity> w = new EntityWrapper<TingchejiaofeiEntity>();
		w.isNotNull("lichangshijian");
		w.ge("lichangshijian", start);
		w.le("lichangshijian", end);
		if (StringUtils.isNotBlank(lotF)) {
			w.eq("tingchechangmingcheng", lotF);
		}
		if (StringUtils.isNotBlank(quyuF)) {
			w.eq("quyu", quyuF);
		}
		List<TingchejiaofeiEntity> list = tingchejiaofeiService.selectList(w);
		double sumPaid = 0.0;
		for (TingchejiaofeiEntity o : list) {
			String lot = nz(o.getTingchechangmingcheng());
			String quyu = nz(o.getQuyu());
			DimAgg a = ensureDim(dimMap, lot, quyu);
			a.lichang++;
			String day = formatDay(o.getLichangshijian());
			if (trendLichang.containsKey(day)) {
				trendLichang.put(day, trendLichang.get(day) + 1);
			}
			double hours = o.getBencitingcheshizhang() != null ? o.getBencitingcheshizhang() : 0.0;
			if (hours <= 0 && o.getJinchangshijian() != null && o.getLichangshijian() != null) {
				hours = (o.getLichangshijian().getTime() - o.getJinchangshijian().getTime()) / (1000.0 * 3600.0);
			}
			if (hours > 0) {
				a.durationSum += hours;
				a.durationCnt++;
			}
			if ("已支付".equals(nz(o.getIspay())) && o.getBencitingchefeiyong() != null) {
				double fee = o.getBencitingchefeiyong();
				a.revenue += fee;
				sumPaid += fee;
				if (trendRev.containsKey(day)) {
					trendRev.put(day, trendRev.get(day) + fee);
				}
			}
		}
		return sumPaid;
	}

	private double aggregateBujiao(Map<String, Double> trendRev, Date start, Date end, String lotF, String quyuF) {
		EntityWrapper<TingcheBujiaoEntity> w = new EntityWrapper<TingcheBujiaoEntity>();
		w.eq("zhuangtai", TingcheBujiaoZhuangtaiN7.YI_ZHIFU);
		w.ge("addtime", start);
		w.le("addtime", end);
		List<TingcheBujiaoEntity> list = tingcheBujiaoDao.selectList(w);
		double sum = 0.0;
		for (TingcheBujiaoEntity b : list) {
			if (StringUtils.isNotBlank(lotF) || StringUtils.isNotBlank(quyuF)) {
				if (b.getChezijinchangId() == null) {
					continue;
				}
				ChezijinchangEntity entry = chezijinchangService.selectById(b.getChezijinchangId());
				if (entry == null) {
					continue;
				}
				if (StringUtils.isNotBlank(lotF) && !lotF.equals(nz(entry.getTingchechangmingcheng()))) {
					continue;
				}
				if (StringUtils.isNotBlank(quyuF) && !quyuF.equals(nz(entry.getQuyu()))) {
					continue;
				}
			}
			double fee = b.getJine() != null ? b.getJine() : 0.0;
			sum += fee;
			String day = formatDay(b.getAddtime());
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
