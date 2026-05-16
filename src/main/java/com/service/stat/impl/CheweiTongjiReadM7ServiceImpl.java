package com.service.stat.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.stat.CheweiTongjiReadDao;
import com.service.stat.CheweiTongjiReadM7Service;

/**
 * 这是M7代码 — 统计只读服务实现。
 * 这是我cursor给父亲写的
 */
@Service("cheweiTongjiReadM7Service")
public class CheweiTongjiReadM7ServiceImpl implements CheweiTongjiReadM7Service {

	@Autowired
	private CheweiTongjiReadDao cheweiTongjiReadDao;

	@Override
	public Map<String, Object> cheweiGlobalSnapshot() {
		Map<String, Object> m = cheweiTongjiReadDao.selectCheweiGlobalSnapshot();
		return m != null ? m : Collections.<String, Object>emptyMap();
	}

	@Override
	public List<Map<String, Object>> listDimChewei(String lot, String quyu) {
		List<Map<String, Object>> list = cheweiTongjiReadDao.selectDimChewei(lot, quyu);
		return list != null ? list : Collections.<Map<String, Object>>emptyList();
	}

	@Override
	public long countYuyue(Date start, Date end) {
		Long n = cheweiTongjiReadDao.countYuyueBetween(start, end);
		return n != null ? n.longValue() : 0L;
	}

	@Override
	public long countRuchang(Date start, Date end) {
		Long n = cheweiTongjiReadDao.countRuchangBetween(start, end);
		return n != null ? n.longValue() : 0L;
	}

	@Override
	public long countLichang(Date start, Date end) {
		Long n = cheweiTongjiReadDao.countLichangBetween(start, end);
		return n != null ? n.longValue() : 0L;
	}

	@Override
	public double sumParkingRevenue(Date start, Date end) {
		Double v = cheweiTongjiReadDao.sumParkingRevenueBetween(start, end);
		return v != null ? v.doubleValue() : 0.0;
	}

	@Override
	public double sumBujiaoRevenue(Date start, Date end) {
		Double v = cheweiTongjiReadDao.sumBujiaoRevenueBetween(start, end);
		return v != null ? v.doubleValue() : 0.0;
	}

	@Override
	public long countYuyueCancel(Date start, Date end) {
		Long n = cheweiTongjiReadDao.countYuyueCancelBetween(start, end);
		return n != null ? n.longValue() : 0L;
	}

	@Override
	public long countChaoshiBujiao(Date start, Date end) {
		Long n = cheweiTongjiReadDao.countChaoshiBujiaoBetween(start, end);
		return n != null ? n.longValue() : 0L;
	}

	@Override
	public List<Map<String, Object>> listRuchangByDim(Date start, Date end, String lot, String quyu) {
		List<Map<String, Object>> list = cheweiTongjiReadDao.countRuchangByDimBetween(start, end, lot, quyu);
		return list != null ? list : Collections.<Map<String, Object>>emptyList();
	}

	@Override
	public List<Map<String, Object>> listRevenueDaily(Date start, Date end, String lot, String quyu) {
		List<Map<String, Object>> list = cheweiTongjiReadDao.selectRevenueDailyBetween(start, end, lot, quyu);
		return list != null ? list : Collections.<Map<String, Object>>emptyList();
	}

	@Override
	public List<Map<String, Object>> listBujiaoDaily(Date start, Date end, String lot, String quyu) {
		List<Map<String, Object>> list = cheweiTongjiReadDao.sumBujiaoDailyBetween(start, end, lot, quyu);
		return list != null ? list : Collections.<Map<String, Object>>emptyList();
	}

	@Override
	public List<Map<String, Object>> listRevenueDim(Date start, Date end, String lot, String quyu) {
		List<Map<String, Object>> list = cheweiTongjiReadDao.selectRevenueDimBetween(start, end, lot, quyu);
		return list != null ? list : Collections.<Map<String, Object>>emptyList();
	}
}
