package com.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.constant.CheweiZhuangtaiYanseN5;
import com.entity.CheweiEntity;
import com.service.CheweiKeshihuaN5Service;
import com.service.CheweiService;
import com.utils.R;

/**
 * 这是N5代码 — 车位可视化实现。
 * 这是我cursor给父亲写的
 */
@Service("cheweiKeshihuaN5Service")
public class CheweiKeshihuaN5ServiceImpl implements CheweiKeshihuaN5Service {

	@Autowired
	private CheweiService cheweiService;

	private static String normalizeQuyu(String quyu) {
		return quyu == null ? "" : quyu.trim();
	}

	private static final Comparator<CheweiEntity> SLOT_ORDER = new Comparator<CheweiEntity>() {
		@Override
		public int compare(CheweiEntity a, CheweiEntity b) {
			int ca = compareNullableInt(a.getWanggeHang(), b.getWanggeHang());
			if (ca != 0) {
				return ca;
			}
			int cb = compareNullableInt(a.getWanggeLie(), b.getWanggeLie());
			if (cb != 0) {
				return cb;
			}
			String na = a.getCheweibianhao() == null ? "" : a.getCheweibianhao();
			String nb = b.getCheweibianhao() == null ? "" : b.getCheweibianhao();
			return na.compareTo(nb);
		}

		private int compareNullableInt(Integer x, Integer y) {
			if (x == null && y == null) {
				return 0;
			}
			if (x == null) {
				return 1;
			}
			if (y == null) {
				return -1;
			}
			return x.compareTo(y);
		}
	};

	@Override
	public R legend() {
		Map<String, Object> data = new LinkedHashMap<String, Object>(2);
		data.put("zhuangtaiColors", CheweiZhuangtaiYanseN5.legendMap());
		data.put("hint", "色块与 N2 状态机一致；未知状态为灰色。");
		return R.ok().put("data", data);
	}

	@Override
	public R slots(String tingchechangmingcheng, String quyu) {
		if (StringUtils.isBlank(tingchechangmingcheng)) {
			return R.error("须传入停车场名称 tingchechangmingcheng");
		}
		String lot = tingchechangmingcheng.trim();
		String quyuNorm = normalizeQuyu(quyu);
		EntityWrapper<CheweiEntity> w = new EntityWrapper<CheweiEntity>();
		w.eq("tingchechangmingcheng", lot);
		w.eq("quyu", quyuNorm);
		List<CheweiEntity> raw = cheweiService.selectList(w);
		List<CheweiEntity> list = new ArrayList<CheweiEntity>(raw);
		Collections.sort(list, SLOT_ORDER);

		boolean allWangge = !list.isEmpty();
		for (CheweiEntity e : list) {
			if (e.getWanggeHang() == null || e.getWanggeLie() == null) {
				allWangge = false;
				break;
			}
		}

		List<Map<String, Object>> slots = new ArrayList<Map<String, Object>>(list.size());
		int maxHang = 0;
		int maxLie = 0;
		for (CheweiEntity e : list) {
			Map<String, Object> m = new HashMap<String, Object>(10);
			m.put("id", e.getId());
			m.put("cheweibianhao", e.getCheweibianhao());
			m.put("zhuangtai", StringUtils.isBlank(e.getZhuangtai()) ? "" : e.getZhuangtai().trim());
			m.put("colorHex", CheweiZhuangtaiYanseN5.colorHexForZhuangtai(e.getZhuangtai()));
			m.put("wanggeHang", e.getWanggeHang());
			m.put("wanggeLie", e.getWanggeLie());
			m.put("beizhu", e.getBeizhu());
			slots.add(m);
			if (e.getWanggeHang() != null && e.getWanggeHang() > maxHang) {
				maxHang = e.getWanggeHang();
			}
			if (e.getWanggeLie() != null && e.getWanggeLie() > maxLie) {
				maxLie = e.getWanggeLie();
			}
		}

		Map<String, Object> data = new LinkedHashMap<String, Object>(8);
		data.put("tingchechangmingcheng", lot);
		data.put("quyu", quyuNorm);
		data.put("total", slots.size());
		data.put("slots", slots);
		data.put("hasFullWanggeLayout", allWangge);
		data.put("wanggeMaxHang", maxHang);
		data.put("wanggeMaxLie", maxLie);
		return R.ok().put("data", data);
	}
}
