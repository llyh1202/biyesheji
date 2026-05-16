package com.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.constant.CheweiYuyueZhuangtaiN4;
import com.constant.CheweiZhuangtaiN2;
import com.constant.YuyueLiuchengJiedianM1;
import com.constant.YuyueZhifuZhuangtaiM1;
import com.dao.CheweiChaoshiGuizeDao;
import com.dao.CheweiYuyueDao;
import com.entity.CheweiChaoshiGuizeEntity;
import com.entity.CheweiEntity;
import com.entity.CheweiYuyueEntity;
import com.entity.TingchejiaofeiEntity;
import com.service.CheweiChaoshiN6Service;
import com.service.CheweiService;
import com.service.TingchejiaofeiService;
import com.utils.R;

/**
 * 这是N6代码 — 超时策略实现：规则匹配、预约超时取消、计费宽限期查询。
 * 这是我cursor给父亲写的
 */
@Service("cheweiChaoshiN6Service")
public class CheweiChaoshiN6ServiceImpl extends ServiceImpl<CheweiChaoshiGuizeDao, CheweiChaoshiGuizeEntity>
		implements CheweiChaoshiN6Service {

	private static final int DEFAULT_BAOLIU_FENZHONG = 30;
	private static final int DEFAULT_KUANXIAN_FENZHONG = 15;

	@Autowired
	private CheweiYuyueDao cheweiYuyueDao;
	@Autowired
	private CheweiService cheweiService;
	@Autowired
	private TingchejiaofeiService tingchejiaofeiService;

	private static String nz(String s) {
		return s == null ? "" : s.trim();
	}

	private static String normalizeQuyu(String quyu) {
		return quyu == null ? "" : quyu.trim();
	}

	private static boolean isQiyong(CheweiChaoshiGuizeEntity g) {
		if (g == null) {
			return false;
		}
		String q = nz(g.getQiyong());
		return StringUtils.isBlank(q) || "是".equals(q) || "1".equals(q) || "true".equalsIgnoreCase(q);
	}

	private static Date addMinutes(Date base, int minutes) {
		if (base == null) {
			return new Date();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(base);
		c.add(Calendar.MINUTE, minutes);
		return c.getTime();
	}

	@Override
	public CheweiChaoshiGuizeEntity resolveRule(String tingchechangmingcheng, String quyu) {
		String lot = nz(tingchechangmingcheng);
		String quyuNorm = normalizeQuyu(quyu);
		CheweiChaoshiGuizeEntity hit = null;
		if (StringUtils.isNotBlank(lot)) {
			EntityWrapper<CheweiChaoshiGuizeEntity> w1 = new EntityWrapper<CheweiChaoshiGuizeEntity>();
			w1.eq("tingchechangmingcheng", lot);
			w1.eq("quyu", quyuNorm);
			w1.eq("qiyong", "是");
			w1.last("ORDER BY id DESC LIMIT 1");
			hit = selectOne(w1);
			if (hit == null && StringUtils.isNotBlank(quyuNorm)) {
				EntityWrapper<CheweiChaoshiGuizeEntity> w2 = new EntityWrapper<CheweiChaoshiGuizeEntity>();
				w2.eq("tingchechangmingcheng", lot);
				w2.andNew().isNull("quyu").or().eq("quyu", "");
				w2.eq("qiyong", "是");
				w2.last("ORDER BY id DESC LIMIT 1");
				hit = selectOne(w2);
			}
		}
		if (hit == null) {
			EntityWrapper<CheweiChaoshiGuizeEntity> wg = new EntityWrapper<CheweiChaoshiGuizeEntity>();
			wg.isNull("tingchechangmingcheng");
			wg.andNew().isNull("quyu").or().eq("quyu", "");
			wg.eq("qiyong", "是");
			wg.last("ORDER BY id DESC LIMIT 1");
			hit = selectOne(wg);
		}
		if (hit != null && isQiyong(hit)) {
			return hit;
		}
		CheweiChaoshiGuizeEntity fallback = new CheweiChaoshiGuizeEntity();
		fallback.setGuizeMingcheng("内置默认");
		fallback.setYuyueBaoliuFenzhong(DEFAULT_BAOLIU_FENZHONG);
		fallback.setJifeiKuanxianFenzhong(DEFAULT_KUANXIAN_FENZHONG);
		fallback.setWeiruchangKoufeiYuan(0.0);
		fallback.setQiyong("是");
		return fallback;
	}

	@Override
	public int resolveJifeiKuanxianFenzhong(String tingchechangmingcheng, String quyu) {
		CheweiChaoshiGuizeEntity g = resolveRule(tingchechangmingcheng, quyu);
		if (g.getJifeiKuanxianFenzhong() != null && g.getJifeiKuanxianFenzhong() >= 0) {
			return g.getJifeiKuanxianFenzhong();
		}
		return DEFAULT_KUANXIAN_FENZHONG;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R processScheduledTimeouts() {
		Date now = new Date();
		EntityWrapper<CheweiYuyueEntity> w = new EntityWrapper<CheweiYuyueEntity>();
		w.eq("zhuangtai", CheweiYuyueZhuangtaiN4.YOUXIAO);
		w.isNull("chezijinchang_id");
		List<CheweiYuyueEntity> list = cheweiYuyueDao.selectList(w);
		int cancelled = 0;
		int penalty = 0;
		for (CheweiYuyueEntity y : list) {
			if (y.getKaishiShijian() == null) {
				continue;
			}
			String lj = nz(y.getLiuchengJiedian());
			if (StringUtils.isNotBlank(lj) && !YuyueLiuchengJiedianM1.YIYUYUE_DAIRUCHANG.equals(lj)) {
				continue;
			}
			CheweiChaoshiGuizeEntity rule = resolveRule(y.getTingchechangmingcheng(), y.getQuyu());
			if (!isQiyong(rule)) {
				continue;
			}
			int hold = rule.getYuyueBaoliuFenzhong() != null ? rule.getYuyueBaoliuFenzhong() : DEFAULT_BAOLIU_FENZHONG;
			Date deadline = addMinutes(y.getKaishiShijian(), hold);
			if (now.before(deadline)) {
				continue;
			}
			if (applyTimeoutForYuyue(y, rule)) {
				cancelled++;
				if (rule.getWeiruchangKoufeiYuan() != null && rule.getWeiruchangKoufeiYuan() > 0) {
					penalty++;
				}
			}
		}
		Map<String, Object> data = new HashMap<String, Object>(4);
		data.put("scanned", list.size());
		data.put("cancelled", cancelled);
		data.put("penaltyOrders", penalty);
		data.put("runAt", now);
		return R.ok().put("data", data);
	}

	/**
	 * 这是N6代码 — 单条预约超时：作废预约、释放车位；若配置违约金则建无离场时间的缴费单（不走 N3 离场链）。
	 */
	private boolean applyTimeoutForYuyue(CheweiYuyueEntity y, CheweiChaoshiGuizeEntity rule) {
		y.setZhuangtai(CheweiYuyueZhuangtaiN4.YI_QUXIAO);
		double fee = rule.getWeiruchangKoufeiYuan() != null ? rule.getWeiruchangKoufeiYuan() : 0.0;
		if (fee > 0) {
			TingchejiaofeiEntity order = new TingchejiaofeiEntity();
			order.setDingdanhao("WY" + System.currentTimeMillis() + y.getId());
			order.setTingchechangmingcheng(y.getTingchechangmingcheng());
			order.setQuyu(y.getQuyu());
			order.setCheweiId(y.getCheweiId());
			order.setBencitingchefeiyong(fee);
			order.setBencitingcheshizhang(0.0);
			order.setIspay("未支付");
			tingchejiaofeiService.insert(order);
			y.setTingchejiaofeiId(order.getId());
			y.setLiuchengJiedian(YuyueLiuchengJiedianM1.CHAOSHI_WEIRUCHANG_DAIFU);
			y.setYuyueZhifuZhuangtai(YuyueZhifuZhuangtaiM1.WEI_ZHIFU);
		} else {
			y.setLiuchengJiedian(YuyueLiuchengJiedianM1.CHAOSHI_ZIDONG_QUXIAO);
		}
		cheweiYuyueDao.updateById(y);
		releaseCheweiIfReservedForYuyue(y);
		return true;
	}

	private void releaseCheweiIfReservedForYuyue(CheweiYuyueEntity y) {
		if (y.getCheweiId() == null) {
			return;
		}
		CheweiEntity cw = cheweiService.selectById(y.getCheweiId());
		if (cw == null) {
			return;
		}
		if (CheweiZhuangtaiN2.YUYUE_WEIRUCHANG.equals(nz(cw.getZhuangtai()))) {
			cw.setZhuangtai(CheweiZhuangtaiN2.KONGXIAN);
			cheweiService.updateById(cw);
		}
	}

	@Override
	public R listGuize() {
		EntityWrapper<CheweiChaoshiGuizeEntity> w = new EntityWrapper<CheweiChaoshiGuizeEntity>();
		w.orderBy("id", false);
		return R.ok().put("data", selectList(w));
	}

	@Override
	public R saveGuize(CheweiChaoshiGuizeEntity row) {
		R v = validateGuize(row);
		if (v != null) {
			return v;
		}
		if (StringUtils.isBlank(row.getQiyong())) {
			row.setQiyong("是");
		}
		row.setAddtime(new Date());
		insert(row);
		return R.ok().put("data", row);
	}

	@Override
	public R updateGuize(CheweiChaoshiGuizeEntity row) {
		if (row.getId() == null) {
			return R.error("须传入规则 id");
		}
		R v = validateGuize(row);
		if (v != null) {
			return v;
		}
		updateById(row);
		return R.ok();
	}

	@Override
	public R deleteGuize(Long id) {
		if (id == null) {
			return R.error("须传入 id");
		}
		deleteById(id);
		return R.ok();
	}

	@Override
	public R resolveRuleSnapshot(String tingchechangmingcheng, String quyu) {
		CheweiChaoshiGuizeEntity g = resolveRule(tingchechangmingcheng, quyu);
		Map<String, Object> data = new HashMap<String, Object>(6);
		data.put("rule", g);
		data.put("yuyueBaoliuFenzhong", g.getYuyueBaoliuFenzhong());
		data.put("jifeiKuanxianFenzhong", g.getJifeiKuanxianFenzhong());
		data.put("weiruchangKoufeiYuan", g.getWeiruchangKoufeiYuan());
		return R.ok().put("data", data);
	}

	private R validateGuize(CheweiChaoshiGuizeEntity row) {
		if (row == null) {
			return R.error("请求体不能为空");
		}
		if (StringUtils.isBlank(row.getGuizeMingcheng())) {
			return R.error("须填写规则名称 guizeMingcheng");
		}
		if (row.getYuyueBaoliuFenzhong() != null && row.getYuyueBaoliuFenzhong() < 0) {
			return R.error("预约保留时长不能为负数");
		}
		if (row.getJifeiKuanxianFenzhong() != null && row.getJifeiKuanxianFenzhong() < 0) {
			return R.error("计费宽限期不能为负数");
		}
		return null;
	}
}
