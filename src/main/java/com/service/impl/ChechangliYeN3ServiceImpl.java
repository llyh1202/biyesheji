package com.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.CheweiEntity;
import com.entity.ChezijinchangEntity;
import com.entity.TingchejiaofeiEntity;
import com.entity.dto.N3TingcheJiesuanDto;
import com.entity.dto.N3TingcheLichangDto;
import com.service.CheweiService;
import com.service.ChechangliYeN3Service;
import com.service.ChezijinchangService;
import com.service.CheweiZhuangtaiN2Service;
import com.service.TingchejiaofeiService;
import com.utils.R;

/**
 * 这是N3代码 — 入场/离场/结算编排实现。
 * 这是我cursor给父亲写的
 */
@Service("chechangliYeN3Service")
public class ChechangliYeN3ServiceImpl implements ChechangliYeN3Service {

	@Autowired
	private ChezijinchangService chezijinchangService;
	@Autowired
	private TingchejiaofeiService tingchejiaofeiService;
	@Autowired
	private CheweiZhuangtaiN2Service cheweiZhuangtaiN2Service;
	@Autowired
	private CheweiService cheweiService;

	private static String nz(String s) {
		if (StringUtils.isBlank(s)) {
			return "";
		}
		return s.trim();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R ruchang(ChezijinchangEntity entry) {
		if (entry == null) {
			return R.error("请求体不能为空");
		}
		if (entry.getCheweiId() == null) {
			return R.error("入场业务须指定车位 cheweiId（与预约/车位主数据绑定）");
		}
		if (entry.getJinchangshijian() == null) {
			entry.setJinchangshijian(new Date());
		}
		chezijinchangService.insert(entry);
		try {
			cheweiZhuangtaiN2Service.afterChezijinchangInserted(entry);
		} catch (IllegalStateException ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return R.error(ex.getMessage());
		}
		return R.ok().put("data", entry);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R lichang(N3TingcheLichangDto body) {
		if (body == null || body.getChezijinchangId() == null) {
			return R.error("须传入入场单 id：chezijinchangId");
		}
		ChezijinchangEntity entry = chezijinchangService.selectById(body.getChezijinchangId());
		if (entry == null) {
			return R.error("入场单不存在");
		}
		if (entry.getCheweiId() == null) {
			return R.error("入场单未关联车位，无法生成离场业务单");
		}
		if (entry.getJinchangshijian() == null) {
			return R.error("入场单缺少进场时间，无法计费离场");
		}
		Date lichang = body.getLichangshijian() != null ? body.getLichangshijian() : new Date();
		if (lichang.before(entry.getJinchangshijian())) {
			return R.error("离场时间不能早于进场时间");
		}
		EntityWrapper<TingchejiaofeiEntity> dup = new EntityWrapper<TingchejiaofeiEntity>();
		dup.eq("crossrefid", body.getChezijinchangId());
		dup.isNotNull("lichangshijian");
		List<TingchejiaofeiEntity> olds = tingchejiaofeiService.selectList(dup);
		for (TingchejiaofeiEntity o : olds) {
			if (!"已支付".equals(nz(o.getIspay()))) {
				return R.error("该入场单已有未支付的离场/缴费单，请先走「结算」关单后再新建离场单");
			}
		}

		int danjia = entry.getXiaoshidanjia() != null && entry.getXiaoshidanjia() > 0 ? entry.getXiaoshidanjia() : 1;
		double hours = (lichang.getTime() - entry.getJinchangshijian().getTime()) / (1000.0 * 3600.0);
		double billHours = Math.max(1.0, Math.ceil(hours - 1e-9));
		double fee = billHours * danjia;

		TingchejiaofeiEntity order = new TingchejiaofeiEntity();
		order.setDingdanhao("TC" + System.currentTimeMillis());
		order.setTingchechangmingcheng(entry.getTingchechangmingcheng());
		order.setQuyu(entry.getQuyu());
		order.setXiaoshidanjia(entry.getXiaoshidanjia());
		order.setYonghuzhanghao(entry.getYonghuzhanghao());
		order.setXingming(entry.getXingming());
		order.setShouji(entry.getShouji());
		order.setChepaihao(entry.getChepaihao());
		order.setCheliangtupian(entry.getCheliangtupian());
		order.setJinchangshijian(entry.getJinchangshijian());
		order.setLichangshijian(lichang);
		order.setBencitingcheshizhang(hours);
		order.setBencitingchefeiyong(fee);
		order.setCrossrefid(entry.getId());
		order.setCheweiId(entry.getCheweiId());
		order.setIspay("未支付");

		tingchejiaofeiService.insert(order);
		try {
			cheweiZhuangtaiN2Service.afterTingchejiaofeiInserted(order);
		} catch (IllegalStateException ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return R.error(ex.getMessage());
		}
		return R.ok().put("data", order);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R jiesuan(N3TingcheJiesuanDto body) {
		if (body == null || body.getTingchejiaofeiId() == null) {
			return R.error("须传入缴费单 id：tingchejiaofeiId");
		}
		Long id = body.getTingchejiaofeiId();
		TingchejiaofeiEntity current = tingchejiaofeiService.selectById(id);
		if (current == null) {
			return R.error("缴费单不存在");
		}
		if (current.getLichangshijian() == null) {
			return R.error("须先完成离场记录（含离场时间）后再结算，不能直接关单");
		}
		if ("已支付".equals(nz(current.getIspay()))) {
			return R.ok().put("data", current).put("msg", "订单已是已支付");
		}
		TingchejiaofeiEntity before = new TingchejiaofeiEntity();
		before.setId(current.getId());
		before.setIspay(current.getIspay());

		current.setIspay("已支付");
		tingchejiaofeiService.updateById(current);
		TingchejiaofeiEntity after = tingchejiaofeiService.selectById(id);
		try {
			cheweiZhuangtaiN2Service.afterTingchejiaofeiUpdatedIfPaid(before, after);
		} catch (IllegalStateException ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return R.error(ex.getMessage());
		}
		return R.ok().put("data", after);
	}

	@Override
	public R chainSummary(Long chezijinchangId) {
		if (chezijinchangId == null) {
			return R.error("须传入 chezijinchangId");
		}
		ChezijinchangEntity entry = chezijinchangService.selectById(chezijinchangId);
		if (entry == null) {
			return R.error("入场单不存在");
		}
		Map<String, Object> data = new HashMap<String, Object>(4);
		data.put("ruchang", entry);
		if (entry.getCheweiId() != null) {
			CheweiEntity cw = cheweiService.selectById(entry.getCheweiId());
			data.put("chewei", cw);
		}
		EntityWrapper<TingchejiaofeiEntity> w = new EntityWrapper<TingchejiaofeiEntity>();
		w.eq("crossrefid", chezijinchangId);
		w.last("ORDER BY id DESC");
		data.put("tingchejiaofeiList", tingchejiaofeiService.selectList(w));
		return R.ok().put("data", data);
	}
}
