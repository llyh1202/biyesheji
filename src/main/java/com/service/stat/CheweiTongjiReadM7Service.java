package com.service.stat;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 这是M7代码 — 统计只读服务（封装 CheweiTongjiReadDao，供 N8/N9 等报表复用）。
 * 这是我cursor给父亲写的
 */
public interface CheweiTongjiReadM7Service {

	Map<String, Object> cheweiGlobalSnapshot();

	List<Map<String, Object>> listDimChewei(String lot, String quyu);

	long countYuyue(Date start, Date end);

	long countRuchang(Date start, Date end);

	long countLichang(Date start, Date end);

	double sumParkingRevenue(Date start, Date end);

	double sumBujiaoRevenue(Date start, Date end);

	long countYuyueCancel(Date start, Date end);

	long countChaoshiBujiao(Date start, Date end);

	List<Map<String, Object>> listRuchangByDim(Date start, Date end, String lot, String quyu);

	List<Map<String, Object>> listRevenueDaily(Date start, Date end, String lot, String quyu);

	List<Map<String, Object>> listBujiaoDaily(Date start, Date end, String lot, String quyu);

	List<Map<String, Object>> listRevenueDim(Date start, Date end, String lot, String quyu);
}
