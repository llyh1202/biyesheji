package com.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.constant.CheweiYuyueZhuangtaiN4;
import com.constant.YuyueLiuchengJiedianM1;
import com.dao.CheweiYuyueDao;
import com.entity.CheweiEntity;
import com.entity.CheweiYuyueEntity;
import com.entity.ChezijinchangEntity;
import com.entity.TingcheBujiaoEntity;
import com.entity.TingchejiaofeiEntity;
import com.entity.dto.P1MyBujiaoItemDto;
import com.entity.dto.P1MyJiaofeiItemDto;
import com.entity.dto.P1MyParkingSummaryDto;
import com.entity.dto.P1MyRuchangItemDto;
import com.entity.dto.P1MyYuyueItemDto;
import com.constant.TingcheBujiaoZhuangtaiN7;
import com.dao.TingcheBujiaoDao;
import com.service.CheweiMyParkingP1Service;
import com.service.CheweiService;
import com.service.ChezijinchangService;
import com.service.TingchejiaofeiService;
import com.utils.R;

/**
 * 这是我cursor给父亲写的 — P1-08 我的停车汇总实现
 */
@Service("cheweiMyParkingP1Service")
public class CheweiMyParkingP1ServiceImpl implements CheweiMyParkingP1Service {

	private static final int LIST_CAP = 50;

	@Autowired
	private CheweiYuyueDao cheweiYuyueDao;
	@Autowired
	private ChezijinchangService chezijinchangService;
	@Autowired
	private TingchejiaofeiService tingchejiaofeiService;
	@Autowired
	private CheweiService cheweiService;
	@Autowired
	private TingcheBujiaoDao tingcheBujiaoDao;

	@Override
	public R myParkingSummary(String yonghuzhanghao) {
		if (StringUtils.isBlank(yonghuzhanghao)) {
			return R.error(401, "请先登录");
		}
		String user = yonghuzhanghao.trim();
		P1MyParkingSummaryDto summary = new P1MyParkingSummaryDto();
		summary.setDaiRuchangYuyueList(loadDaiRuchangYuyue(user));
		summary.setZaiChangRuchangList(loadZaiChangRuchang(user));
		summary.setDaiZhifuJiaofeiList(loadDaiZhifuJiaofei(user));
		// 这是我cursor给父亲写的 — P1-25 待补缴（N7 待支付）
		summary.setDaiBujiaoList(loadDaiBujiao(user));
		return R.ok().put("data", summary);
	}

	private List<P1MyYuyueItemDto> loadDaiRuchangYuyue(String user) {
		EntityWrapper<CheweiYuyueEntity> w = new EntityWrapper<CheweiYuyueEntity>();
		w.eq("yonghuzhanghao", user);
		w.eq("zhuangtai", CheweiYuyueZhuangtaiN4.YOUXIAO);
		w.eq("liucheng_jiedian", YuyueLiuchengJiedianM1.YIYUYUE_DAIRUCHANG);
		w.isNull("chezijinchang_id");
		w.orderBy("id", false);
		w.last("LIMIT " + LIST_CAP);
		List<CheweiYuyueEntity> rows = cheweiYuyueDao.selectList(w);
		Map<Long, CheweiEntity> cheweiMap = loadCheweiMap(collectCheweiIdsFromYuyue(rows));
		List<P1MyYuyueItemDto> list = new ArrayList<P1MyYuyueItemDto>(rows.size());
		for (CheweiYuyueEntity y : rows) {
			P1MyYuyueItemDto item = new P1MyYuyueItemDto();
			item.setId(y.getId());
			item.setCheweiId(y.getCheweiId());
			item.setTingchechangmingcheng(y.getTingchechangmingcheng());
			item.setQuyu(y.getQuyu());
			item.setKaishiShijian(y.getKaishiShijian());
			item.setJieshuShijian(y.getJieshuShijian());
			item.setZhuangtai(y.getZhuangtai());
			item.setLiuchengJiedian(y.getLiuchengJiedian());
			if (y.getCheweiId() != null) {
				CheweiEntity cw = cheweiMap.get(y.getCheweiId());
				if (cw != null) {
					item.setCheweibianhao(cw.getCheweibianhao());
				}
			}
			list.add(item);
		}
		return list;
	}

	private List<P1MyRuchangItemDto> loadZaiChangRuchang(String user) {
		EntityWrapper<ChezijinchangEntity> w = new EntityWrapper<ChezijinchangEntity>();
		w.eq("yonghuzhanghao", user);
		w.orderBy("id", false);
		w.last("LIMIT " + (LIST_CAP * 3));
		List<ChezijinchangEntity> entries = chezijinchangService.selectList(w);
		if (entries == null || entries.isEmpty()) {
			return new ArrayList<P1MyRuchangItemDto>(0);
		}
		List<Long> entryIds = new ArrayList<Long>(entries.size());
		for (ChezijinchangEntity c : entries) {
			if (c.getId() != null) {
				entryIds.add(c.getId());
			}
		}
		Set<Long> exitedIds = loadExitedEntryIds(entryIds);
		Map<Long, CheweiEntity> cheweiMap = loadCheweiMap(collectCheweiIdsFromRuchang(entries));
		List<P1MyRuchangItemDto> list = new ArrayList<P1MyRuchangItemDto>();
		for (ChezijinchangEntity c : entries) {
			if (c.getId() != null && exitedIds.contains(c.getId())) {
				continue;
			}
			P1MyRuchangItemDto item = new P1MyRuchangItemDto();
			item.setId(c.getId());
			item.setCheweiId(c.getCheweiId());
			item.setTingchechangmingcheng(c.getTingchechangmingcheng());
			item.setQuyu(c.getQuyu());
			item.setChepaihao(c.getChepaihao());
			item.setXingming(c.getXingming());
			item.setJinchangshijian(c.getJinchangshijian());
			if (c.getCheweiId() != null) {
				CheweiEntity cw = cheweiMap.get(c.getCheweiId());
				if (cw != null) {
					item.setCheweibianhao(cw.getCheweibianhao());
				}
			}
			list.add(item);
			if (list.size() >= LIST_CAP) {
				break;
			}
		}
		return list;
	}

	private Set<Long> loadExitedEntryIds(List<Long> entryIds) {
		Set<Long> exited = new HashSet<Long>();
		if (entryIds == null || entryIds.isEmpty()) {
			return exited;
		}
		EntityWrapper<TingchejiaofeiEntity> fw = new EntityWrapper<TingchejiaofeiEntity>();
		fw.in("crossrefid", entryIds);
		fw.isNotNull("crossrefid");
		List<TingchejiaofeiEntity> fees = tingchejiaofeiService.selectList(fw);
		if (fees != null) {
			for (TingchejiaofeiEntity f : fees) {
				if (f.getCrossrefid() != null) {
					exited.add(f.getCrossrefid());
				}
			}
		}
		return exited;
	}

	private List<P1MyBujiaoItemDto> loadDaiBujiao(String user) {
		EntityWrapper<TingcheBujiaoEntity> w = new EntityWrapper<TingcheBujiaoEntity>();
		w.eq("yonghuzhanghao", user);
		w.eq("zhuangtai", TingcheBujiaoZhuangtaiN7.DAI_ZHIFU);
		w.orderBy("id", false);
		w.last("LIMIT " + LIST_CAP);
		List<TingcheBujiaoEntity> rows = tingcheBujiaoDao.selectList(w);
		if (rows == null || rows.isEmpty()) {
			return new ArrayList<P1MyBujiaoItemDto>(0);
		}
		Map<Long, ChezijinchangEntity> entryMap = loadRuchangMap(collectEntryIdsFromBujiao(rows));
		List<P1MyBujiaoItemDto> list = new ArrayList<P1MyBujiaoItemDto>(rows.size());
		for (TingcheBujiaoEntity b : rows) {
			P1MyBujiaoItemDto item = new P1MyBujiaoItemDto();
			item.setId(b.getId());
			item.setDanhao(b.getDanhao());
			item.setChezijinchangId(b.getChezijinchangId());
			item.setLeixing(b.getLeixing());
			item.setJine(b.getJine());
			item.setZhuangtai(b.getZhuangtai());
			item.setYuanyin(b.getYuanyin());
			item.setChepaihao(b.getChepaihao());
			if (b.getChezijinchangId() != null) {
				ChezijinchangEntity entry = entryMap.get(b.getChezijinchangId());
				if (entry != null) {
					item.setTingchechangmingcheng(entry.getTingchechangmingcheng());
					item.setQuyu(entry.getQuyu());
					if (StringUtils.isBlank(item.getChepaihao())) {
						item.setChepaihao(entry.getChepaihao());
					}
				}
			}
			list.add(item);
		}
		return list;
	}

	private Set<Long> collectEntryIdsFromBujiao(List<TingcheBujiaoEntity> rows) {
		Set<Long> ids = new HashSet<Long>();
		if (rows != null) {
			for (TingcheBujiaoEntity b : rows) {
				if (b.getChezijinchangId() != null) {
					ids.add(b.getChezijinchangId());
				}
			}
		}
		return ids;
	}

	private Map<Long, ChezijinchangEntity> loadRuchangMap(Set<Long> entryIds) {
		Map<Long, ChezijinchangEntity> map = new HashMap<Long, ChezijinchangEntity>();
		if (entryIds == null || entryIds.isEmpty()) {
			return map;
		}
		List<ChezijinchangEntity> list = chezijinchangService.selectBatchIds(new ArrayList<Long>(entryIds));
		if (list != null) {
			for (ChezijinchangEntity c : list) {
				if (c.getId() != null) {
					map.put(c.getId(), c);
				}
			}
		}
		return map;
	}

	private List<P1MyJiaofeiItemDto> loadDaiZhifuJiaofei(String user) {
		EntityWrapper<TingchejiaofeiEntity> w = new EntityWrapper<TingchejiaofeiEntity>();
		w.eq("yonghuzhanghao", user);
		w.ne("ispay", "已支付");
		w.isNotNull("lichangshijian");
		w.orderBy("id", false);
		w.last("LIMIT " + LIST_CAP);
		List<TingchejiaofeiEntity> rows = tingchejiaofeiService.selectList(w);
		List<P1MyJiaofeiItemDto> list = new ArrayList<P1MyJiaofeiItemDto>(rows.size());
		for (TingchejiaofeiEntity o : rows) {
			P1MyJiaofeiItemDto item = new P1MyJiaofeiItemDto();
			item.setId(o.getId());
			item.setDingdanhao(o.getDingdanhao());
			item.setCheweiId(o.getCheweiId());
			item.setChezijinchangId(o.getCrossrefid());
			item.setTingchechangmingcheng(o.getTingchechangmingcheng());
			item.setQuyu(o.getQuyu());
			item.setChepaihao(o.getChepaihao());
			item.setJinchangshijian(o.getJinchangshijian());
			item.setLichangshijian(o.getLichangshijian());
			item.setBencitingcheshizhang(o.getBencitingcheshizhang());
			item.setBencitingchefeiyong(o.getBencitingchefeiyong());
			item.setIspay(o.getIspay());
			list.add(item);
		}
		return list;
	}

	private Map<Long, CheweiEntity> loadCheweiMap(Set<Long> ids) {
		Map<Long, CheweiEntity> map = new HashMap<Long, CheweiEntity>();
		if (ids == null || ids.isEmpty()) {
			return map;
		}
		List<CheweiEntity> list = cheweiService.selectBatchIds(new ArrayList<Long>(ids));
		if (list != null) {
			for (CheweiEntity c : list) {
				if (c.getId() != null) {
					map.put(c.getId(), c);
				}
			}
		}
		return map;
	}

	private Set<Long> collectCheweiIdsFromYuyue(List<CheweiYuyueEntity> rows) {
		Set<Long> ids = new HashSet<Long>();
		if (rows != null) {
			for (CheweiYuyueEntity y : rows) {
				if (y.getCheweiId() != null) {
					ids.add(y.getCheweiId());
				}
			}
		}
		return ids;
	}

	private Set<Long> collectCheweiIdsFromRuchang(List<ChezijinchangEntity> rows) {
		Set<Long> ids = new HashSet<Long>();
		if (rows != null) {
			for (ChezijinchangEntity c : rows) {
				if (c.getCheweiId() != null) {
					ids.add(c.getCheweiId());
				}
			}
		}
		return ids;
	}
}
