package com.service.impl;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.constant.CheweiYuyueZhuangtaiN4;
import com.constant.CheweiZhuangtaiN2;
import com.constant.TingcheBujiaoZhuangtaiN7;
import com.dao.CheweiYuyueDao;
import com.dao.TingcheBujiaoDao;
import com.entity.CheweiEntity;
import com.entity.CheweiYuyueEntity;
import com.entity.ChezijinchangEntity;
import com.entity.TingcheBujiaoEntity;
import com.entity.TingchejiaofeiEntity;
import com.entity.dto.N8KanbanOverviewDto;
import com.entity.dto.N8PeriodKpiDto;
import com.service.CheweiService;
import com.service.ChezijinchangService;
import com.service.TingchejiaofeiService;
import com.service.YunyingKanbanN8Service;
import com.utils.R;

/**
 * 这是N8代码 — 今日/本周 KPI 聚合（预约、入场、离场、收入、利用率、超时）。
 * 这是我cursor给父亲写的
 */
@Service("yunyingKanbanN8Service")
public class YunyingKanbanN8ServiceImpl implements YunyingKanbanN8Service {

	private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

	@Autowired
	private CheweiYuyueDao cheweiYuyueDao;
	@Autowired
	private ChezijinchangService chezijinchangService;
	@Autowired
	private TingchejiaofeiService tingchejiaofeiService;
	@Autowired
	private TingcheBujiaoDao tingcheBujiaoDao;
	@Autowired
	private CheweiService cheweiService;

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
		EntityWrapper<CheweiYuyueEntity> w = new EntityWrapper<CheweiYuyueEntity>();
		w.ge("addtime", start);
		w.le("addtime", end);
		return cheweiYuyueDao.selectCount(w);
	}

	private long countRuchang(Date start, Date end) {
		EntityWrapper<ChezijinchangEntity> w = new EntityWrapper<ChezijinchangEntity>();
		w.ge("jinchangshijian", start);
		w.le("jinchangshijian", end);
		return chezijinchangService.selectCount(w);
	}

	private long countLichang(Date start, Date end) {
		EntityWrapper<TingchejiaofeiEntity> w = new EntityWrapper<TingchejiaofeiEntity>();
		w.isNotNull("lichangshijian");
		w.ge("lichangshijian", start);
		w.le("lichangshijian", end);
		return tingchejiaofeiService.selectCount(w);
	}

	private double sumParkingRevenue(Date start, Date end) {
		EntityWrapper<TingchejiaofeiEntity> w = new EntityWrapper<TingchejiaofeiEntity>();
		w.setSqlSelect("IFNULL(SUM(bencitingchefeiyong),0) AS total");
		w.eq("ispay", "已支付");
		w.isNotNull("lichangshijian");
		w.ge("lichangshijian", start);
		w.le("lichangshijian", end);
		return extractSum(tingchejiaofeiService.selectMaps(w));
	}

	private double sumBujiaoRevenue(Date start, Date end) {
		EntityWrapper<TingcheBujiaoEntity> w = new EntityWrapper<TingcheBujiaoEntity>();
		w.setSqlSelect("IFNULL(SUM(jine),0) AS total");
		w.eq("zhuangtai", TingcheBujiaoZhuangtaiN7.YI_ZHIFU);
		w.ge("addtime", start);
		w.le("addtime", end);
		return extractSum(tingcheBujiaoDao.selectMaps(w));
	}

	private long countChaoshiCancel(Date start, Date end) {
		EntityWrapper<CheweiYuyueEntity> w = new EntityWrapper<CheweiYuyueEntity>();
		w.eq("zhuangtai", CheweiYuyueZhuangtaiN4.YI_QUXIAO);
		w.ge("addtime", start);
		w.le("addtime", end);
		return cheweiYuyueDao.selectCount(w);
	}

	private long countChaoshiBujiao(Date start, Date end) {
		EntityWrapper<TingcheBujiaoEntity> w = new EntityWrapper<TingcheBujiaoEntity>();
		w.eq("leixing", "超时补缴");
		w.ge("addtime", start);
		w.le("addtime", end);
		return tingcheBujiaoDao.selectCount(w);
	}

	private void fillCheweiSnapshot(N8KanbanOverviewDto dto) {
		long total = cheweiService.selectCount(new EntityWrapper<CheweiEntity>());
		EntityWrapper<CheweiEntity> freeW = new EntityWrapper<CheweiEntity>();
		freeW.eq("zhuangtai", CheweiZhuangtaiN2.KONGXIAN);
		long free = cheweiService.selectCount(freeW);
		long occupied = Math.max(0, total - free);
		dto.setCheweiTotal(total);
		dto.setCheweiOccupied(occupied);
		dto.setUtilizationRate(total <= 0 ? 0.0 : round2(occupied * 100.0 / total));
	}

	private static double extractSum(List<Map<String, Object>> maps) {
		if (maps == null || maps.isEmpty() || maps.get(0) == null) {
			return 0.0;
		}
		Object v = maps.get(0).get("total");
		if (v == null) {
			v = maps.get(0).get("TOTAL");
		}
		return v == null ? 0.0 : ((Number) v).doubleValue();
	}

	private static Date toDate(LocalDateTime ldt) {
		return Date.from(ldt.atZone(ZONE).toInstant());
	}

	private static double round2(double v) {
		return Math.round(v * 100.0) / 100.0;
	}
}
