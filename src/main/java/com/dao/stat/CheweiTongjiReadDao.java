package com.dao.stat;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * 这是M7代码 — 统计只读 Dao（仅 SELECT / 视图，禁止写操作）。
 * 这是我cursor给父亲写的
 */
public interface CheweiTongjiReadDao {

	Map<String, Object> selectCheweiGlobalSnapshot();

	List<Map<String, Object>> selectDimChewei(@Param("lot") String lot, @Param("quyu") String quyu);

	Long countYuyueBetween(@Param("start") Date start, @Param("end") Date end);

	Long countRuchangBetween(@Param("start") Date start, @Param("end") Date end);

	Long countLichangBetween(@Param("start") Date start, @Param("end") Date end);

	Double sumParkingRevenueBetween(@Param("start") Date start, @Param("end") Date end);

	Double sumBujiaoRevenueBetween(@Param("start") Date start, @Param("end") Date end);

	Long countYuyueCancelBetween(@Param("start") Date start, @Param("end") Date end);

	Long countChaoshiBujiaoBetween(@Param("start") Date start, @Param("end") Date end);

	List<Map<String, Object>> countRuchangByDimBetween(@Param("start") Date start, @Param("end") Date end,
			@Param("lot") String lot, @Param("quyu") String quyu);

	List<Map<String, Object>> selectRevenueDailyBetween(@Param("start") Date start, @Param("end") Date end,
			@Param("lot") String lot, @Param("quyu") String quyu);

	List<Map<String, Object>> sumBujiaoDailyBetween(@Param("start") Date start, @Param("end") Date end,
			@Param("lot") String lot, @Param("quyu") String quyu);

	List<Map<String, Object>> selectRevenueDimBetween(@Param("start") Date start, @Param("end") Date end,
			@Param("lot") String lot, @Param("quyu") String quyu);
}
