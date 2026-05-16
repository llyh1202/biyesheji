package com.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.constant.JifeiMoshiM5;
import com.dao.CheweiJifeiGuizeDao;
import com.entity.CheweiJifeiGuizeEntity;
import com.entity.dto.M5JifeiCalcDto;
import com.service.CheweiChaoshiN6Service;
import com.service.TingcheJifeiM5Service;
import com.utils.R;

/**
 * 这是M5代码 — 费用计算规则实现（首小时、阶梯、封顶）。
 * 这是我cursor给父亲写的
 */
@Service("tingcheJifeiM5Service")
public class TingcheJifeiM5ServiceImpl extends ServiceImpl<CheweiJifeiGuizeDao, CheweiJifeiGuizeEntity>
		implements TingcheJifeiM5Service {

	private static final double DEFAULT_MIN_HOURS = 1.0;

	@Autowired
	private CheweiChaoshiN6Service cheweiChaoshiN6Service;

	private static String nz(String s) {
		return s == null ? "" : s.trim();
	}

	private static String normalizeQuyu(String quyu) {
		return quyu == null ? "" : quyu.trim();
	}

	private static boolean isQiyong(CheweiJifeiGuizeEntity g) {
		if (g == null) {
			return false;
		}
		String q = nz(g.getQiyong());
		return StringUtils.isBlank(q) || "是".equals(q) || "1".equals(q) || "true".equalsIgnoreCase(q);
	}

	@Override
	public CheweiJifeiGuizeEntity resolveRule(String tingchechangmingcheng, String quyu) {
		String lot = nz(tingchechangmingcheng);
		String quyuNorm = normalizeQuyu(quyu);
		CheweiJifeiGuizeEntity hit = null;
		if (StringUtils.isNotBlank(lot)) {
			EntityWrapper<CheweiJifeiGuizeEntity> w1 = new EntityWrapper<CheweiJifeiGuizeEntity>();
			w1.eq("tingchechangmingcheng", lot);
			w1.eq("quyu", quyuNorm);
			w1.eq("qiyong", "是");
			w1.last("ORDER BY id DESC LIMIT 1");
			hit = selectOne(w1);
			if (hit == null && StringUtils.isNotBlank(quyuNorm)) {
				EntityWrapper<CheweiJifeiGuizeEntity> w2 = new EntityWrapper<CheweiJifeiGuizeEntity>();
				w2.eq("tingchechangmingcheng", lot);
				w2.andNew().isNull("quyu").or().eq("quyu", "");
				w2.eq("qiyong", "是");
				w2.last("ORDER BY id DESC LIMIT 1");
				hit = selectOne(w2);
			}
		}
		if (hit == null) {
			EntityWrapper<CheweiJifeiGuizeEntity> wg = new EntityWrapper<CheweiJifeiGuizeEntity>();
			wg.isNull("tingchechangmingcheng");
			wg.andNew().isNull("quyu").or().eq("quyu", "");
			wg.eq("qiyong", "是");
			wg.last("ORDER BY id DESC LIMIT 1");
			hit = selectOne(wg);
		}
		if (hit != null && isQiyong(hit)) {
			return hit;
		}
		CheweiJifeiGuizeEntity fallback = new CheweiJifeiGuizeEntity();
		fallback.setGuizeMingcheng("内置默认纯小时");
		fallback.setJifeiMoshi(JifeiMoshiM5.CHUN_XIAOSHI);
		fallback.setZuixiaoJifeiXiaoshi(DEFAULT_MIN_HOURS);
		fallback.setQiyong("是");
		return fallback;
	}

	@Override
	public R calculate(M5JifeiCalcDto body) {
		if (body == null || body.getJinchangshijian() == null || body.getLichangshijian() == null) {
			return R.error("须传入进场时间 jinchangshijian 与离场时间 lichangshijian");
		}
		if (!body.getLichangshijian().after(body.getJinchangshijian())
				&& !body.getLichangshijian().equals(body.getJinchangshijian())) {
			return R.error("离场时间不能早于进场时间");
		}
		return calculateParkingFee(body.getJinchangshijian(), body.getLichangshijian(),
				body.getTingchechangmingcheng(), body.getQuyu(), body.getXiaoshidanjia());
	}

	@Override
	public R calculateParkingFee(Date jinchang, Date lichang, String lot, String quyu, Integer entryHourlyPrice) {
		String lotNorm = nz(lot);
		String quyuNorm = normalizeQuyu(quyu);
		double rawHours = (lichang.getTime() - jinchang.getTime()) / (1000.0 * 3600.0);
		// 这是N6代码 — 宽限期从计费时长扣减（与离场结算共用）
		int graceMin = cheweiChaoshiN6Service.resolveJifeiKuanxianFenzhong(lotNorm, quyuNorm);
		double chargeableHours = Math.max(0.0, rawHours - graceMin / 60.0);

		CheweiJifeiGuizeEntity rule = resolveRule(lotNorm, quyuNorm);
		int danjia = entryHourlyPrice != null && entryHourlyPrice > 0 ? entryHourlyPrice : 1;
		if (rule.getMeixiaoshiDanjia() != null && rule.getMeixiaoshiDanjia() > 0) {
			danjia = rule.getMeixiaoshiDanjia();
		}
		double minHours = rule.getZuixiaoJifeiXiaoshi() != null && rule.getZuixiaoJifeiXiaoshi() > 0
				? rule.getZuixiaoJifeiXiaoshi() : DEFAULT_MIN_HOURS;
		double billHours = Math.max(minHours, Math.ceil(chargeableHours - 1e-9));

		String moshi = StringUtils.isBlank(rule.getJifeiMoshi()) ? JifeiMoshiM5.CHUN_XIAOSHI : rule.getJifeiMoshi().trim();
		double feeBeforeCap;
		String breakdown;

		if (JifeiMoshiM5.SHOU_XIAOSHI.equals(moshi) || JifeiMoshiM5.JIETI.equals(moshi)) {
			double firstHourFee = rule.getShouxiaoshiYuan() != null && rule.getShouxiaoshiYuan() > 0
					? rule.getShouxiaoshiYuan() : danjia;
			int tierDanjia = rule.getJietiDanjia() != null && rule.getJietiDanjia() > 0 ? rule.getJietiDanjia() : danjia;
			if (billHours <= 1.0) {
				feeBeforeCap = firstHourFee;
				breakdown = "首小时 " + firstHourFee + " 元";
			} else {
				double extraHours = Math.ceil(billHours - 1.0);
				double extraFee = extraHours * tierDanjia;
				feeBeforeCap = firstHourFee + extraFee;
				breakdown = "首小时 " + firstHourFee + " 元 + 后续 " + (int) extraHours + " 小时×" + tierDanjia + " 元";
			}
		} else {
			feeBeforeCap = billHours * danjia;
			breakdown = "纯小时 " + billHours + " 小时×" + danjia + " 元";
		}

		double fee = feeBeforeCap;
		Double cap = rule.getFengdingYuan();
		boolean capped = false;
		if (cap != null && cap > 0 && fee > cap) {
			fee = cap;
			capped = true;
		}

		Map<String, Object> data = new HashMap<String, Object>(16);
		data.put("rule", rule);
		data.put("jifeiMoshi", moshi);
		data.put("rawHours", rawHours);
		data.put("graceMinutes", graceMin);
		data.put("chargeableHours", chargeableHours);
		data.put("billHours", billHours);
		data.put("danjia", danjia);
		data.put("feeBeforeCap", feeBeforeCap);
		data.put("fee", fee);
		data.put("fengdingYuan", cap);
		data.put("capped", capped);
		data.put("breakdown", breakdown);
		return R.ok().put("data", data);
	}

	@Override
	public R listGuize() {
		EntityWrapper<CheweiJifeiGuizeEntity> w = new EntityWrapper<CheweiJifeiGuizeEntity>();
		w.orderBy("id", false);
		return R.ok().put("data", selectList(w));
	}

	@Override
	public R saveGuize(CheweiJifeiGuizeEntity row) {
		R v = validateGuize(row);
		if (v != null) {
			return v;
		}
		if (StringUtils.isBlank(row.getQiyong())) {
			row.setQiyong("是");
		}
		if (row.getZuixiaoJifeiXiaoshi() == null) {
			row.setZuixiaoJifeiXiaoshi(DEFAULT_MIN_HOURS);
		}
		row.setAddtime(new Date());
		insert(row);
		return R.ok().put("data", row);
	}

	@Override
	public R updateGuize(CheweiJifeiGuizeEntity row) {
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
		CheweiJifeiGuizeEntity g = resolveRule(tingchechangmingcheng, quyu);
		return R.ok().put("data", g);
	}

	private R validateGuize(CheweiJifeiGuizeEntity row) {
		if (row == null) {
			return R.error("请求体不能为空");
		}
		if (StringUtils.isBlank(row.getGuizeMingcheng())) {
			return R.error("须填写规则名称");
		}
		if (StringUtils.isBlank(row.getJifeiMoshi())) {
			row.setJifeiMoshi(JifeiMoshiM5.CHUN_XIAOSHI);
		}
		if (row.getFengdingYuan() != null && row.getFengdingYuan() < 0) {
			return R.error("封顶金额不能为负数");
		}
		return null;
	}
}
