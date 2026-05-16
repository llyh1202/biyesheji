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
import com.constant.CheweiYuyueZhuangtaiN4;
import com.constant.YuyueLiuchengJiedianM1;
import com.dao.CheweiYuyueDao;
import com.entity.CheweiEntity;
import com.entity.CheweiYuyueEntity;
import com.entity.CheweixinxiEntity;
import com.entity.ChezijinchangEntity;
import com.entity.TingchejiaofeiEntity;
import com.entity.dto.M2RuchangDto;
import com.entity.dto.N3TingcheJiesuanDto;
import com.entity.dto.N3TingcheLichangDto;
import com.service.TingcheBujiaoN7Service;
import com.service.TingcheJifeiM5Service;
import com.service.CheweiService;
import com.service.ChechangliYeN3Service;
import com.service.ChezijinchangService;
import com.service.CheweixinxiService;
import com.service.CheweiZhuangtaiN2Service;
import com.service.TingchejiaofeiService;
import com.utils.R;

/**
 * 这是N3代码 — 入场/离场/结算编排实现；这是M2/M3/M5/N7代码 — 预约入场、M5 计费、补缴并入。
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
	@Autowired
	private CheweiYuyueDao cheweiYuyueDao;
	@Autowired
	private CheweixinxiService cheweixinxiService;
	@Autowired
	private TingcheJifeiM5Service tingcheJifeiM5Service;
	@Autowired
	private TingcheBujiaoN7Service tingcheBujiaoN7Service;

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
	public R m2YuyueSnapshot(Long yuyueId) {
		if (yuyueId == null) {
			return R.error("须传入预约单 id：yuyueId");
		}
		CheweiYuyueEntity yuyue = cheweiYuyueDao.selectById(yuyueId);
		if (yuyue == null) {
			return R.error("预约单不存在");
		}
		Map<String, Object> data = new HashMap<String, Object>(6);
		data.put("yuyue", yuyue);
		if (yuyue.getCheweiId() != null) {
			CheweiEntity cw = cheweiService.selectById(yuyue.getCheweiId());
			data.put("chewei", cw);
			if (cw != null && cw.getCheweixinxiId() != null) {
				CheweixinxiEntity info = cheweixinxiService.selectById(cw.getCheweixinxiId());
				data.put("cheweixinxi", info);
			}
		}
		data.put("hint", "确认时段与车位后，在 M2 页面提交入场；离场请用本系统「生成缴费单」接口，勿手工新建无入场关联的缴费单。");
		return R.ok().put("data", data);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R m2RuchangByYuyue(M2RuchangDto body) {
		if (body == null || body.getYuyueId() == null) {
			return R.error("须传入预约单 id：yuyueId");
		}
		if (StringUtils.isBlank(body.getChepaihao())) {
			return R.error("须填写车牌号 chepaihao");
		}
		CheweiYuyueEntity yuyue = cheweiYuyueDao.selectById(body.getYuyueId());
		if (yuyue == null) {
			return R.error("预约单不存在");
		}
		if (!CheweiYuyueZhuangtaiN4.YOUXIAO.equals(nz(yuyue.getZhuangtai()))) {
			return R.error("预约单无效或已取消，无法入场");
		}
		if (yuyue.getCheweiId() == null) {
			return R.error("预约单未关联车位");
		}
		if (yuyue.getChezijinchangId() != null) {
			return R.error("该预约已关联入场单，请勿重复入场");
		}
		String lj = nz(yuyue.getLiuchengJiedian());
		if (StringUtils.isNotBlank(lj) && !YuyueLiuchengJiedianM1.YIYUYUE_DAIRUCHANG.equals(lj)) {
			return R.error("预约单流程节点须为「已预约待入场」，当前为「" + lj + "」");
		}
		CheweiEntity cw = cheweiService.selectById(yuyue.getCheweiId());
		if (cw == null) {
			return R.error("车位不存在");
		}
		Date jt = body.getJinchangshijian() != null ? body.getJinchangshijian() : new Date();
		if (yuyue.getKaishiShijian() != null && jt.getTime() < yuyue.getKaishiShijian().getTime()) {
			return R.error("未到预约时段开始时间，不允许入场");
		}
		if (yuyue.getJieshuShijian() != null && jt.getTime() >= yuyue.getJieshuShijian().getTime()) {
			return R.error("已超过预约时段结束时间，不允许入场");
		}
		int danjia = 1;
		if (cw.getCheweixinxiId() != null) {
			CheweixinxiEntity info = cheweixinxiService.selectById(cw.getCheweixinxiId());
			if (info != null && info.getXiaoshidanjia() != null && info.getXiaoshidanjia() > 0) {
				danjia = info.getXiaoshidanjia();
			}
		}
		String lot = StringUtils.isNotBlank(yuyue.getTingchechangmingcheng()) ? yuyue.getTingchechangmingcheng()
				: nz(cw.getTingchechangmingcheng());
		String quyu = StringUtils.isNotBlank(yuyue.getQuyu()) ? yuyue.getQuyu() : nz(cw.getQuyu());
		ChezijinchangEntity entry = new ChezijinchangEntity();
		entry.setCheweiId(yuyue.getCheweiId());
		entry.setTingchechangmingcheng(lot);
		entry.setQuyu(quyu);
		entry.setCheweishuliang(1);
		entry.setXiaoshidanjia(danjia);
		entry.setYonghuzhanghao(nz(body.getYonghuzhanghao()));
		entry.setXingming(nz(body.getXingming()));
		entry.setShouji(nz(body.getShouji()));
		entry.setChepaihao(nz(body.getChepaihao()));
		entry.setCheliangtupian(nz(body.getCheliangtupian()));
		entry.setJinchangshijian(jt);
		chezijinchangService.insert(entry);
		try {
			cheweiZhuangtaiN2Service.afterChezijinchangInsertedForM2(entry, body.getYuyueId());
		} catch (IllegalStateException ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return R.error(ex.getMessage());
		}
		Map<String, Object> payload = new HashMap<String, Object>(4);
		payload.put("ruchang", entry);
		payload.put("yuyueId", body.getYuyueId());
		payload.put("nextLichangUrl", "/n3/tingcheli/lichang");
		payload.put("nextJiesuanUrl", "/n3/tingcheli/jiesuan");
		return R.ok().put("data", payload);
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
		if (body.getYuyueId() != null) {
			CheweiYuyueEntity y = cheweiYuyueDao.selectById(body.getYuyueId());
			if (y == null) {
				return R.error("预约单不存在");
			}
			if (y.getChezijinchangId() == null || !y.getChezijinchangId().equals(body.getChezijinchangId())) {
				return R.error("预约单与入场单不匹配");
			}
			if (y.getCheweiId() == null || !y.getCheweiId().equals(entry.getCheweiId())) {
				return R.error("预约单与入场单车位不一致");
			}
			String jn = nz(y.getLiuchengJiedian());
			if (StringUtils.isNotBlank(jn) && !YuyueLiuchengJiedianM1.YIRUCHANG.equals(jn)) {
				return R.error("预约单须在「已入场」后方可离场结算，当前为「" + jn + "」");
			}
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
		try {
			// 这是N7代码 — 离场前须结清待支付补缴单
			tingcheBujiaoN7Service.assertNoUnpaidBeforeLichang(body.getChezijinchangId());
		} catch (IllegalStateException ex) {
			return R.error(ex.getMessage());
		}

		// 这是M5代码 — 统一计费入口（含 N6 宽限 + 首小时/阶梯/封顶）
		R calcR = tingcheJifeiM5Service.calculateParkingFee(entry.getJinchangshijian(), lichang,
				entry.getTingchechangmingcheng(), entry.getQuyu(), entry.getXiaoshidanjia());
		if (calcR == null || !Integer.valueOf(0).equals(calcR.get("code"))) {
			return R.error(calcR != null && calcR.get("msg") != null ? calcR.get("msg").toString() : "计费失败");
		}
		@SuppressWarnings("unchecked")
		Map<String, Object> calc = (Map<String, Object>) calcR.get("data");
		double fee = calc.get("fee") != null ? ((Number) calc.get("fee")).doubleValue() : 0.0;
		double chargeableHours = calc.get("chargeableHours") != null
				? ((Number) calc.get("chargeableHours")).doubleValue() : 0.0;
		Object danjiaObj = calc.get("danjia");
		int danjia = danjiaObj != null ? ((Number) danjiaObj).intValue()
				: (entry.getXiaoshidanjia() != null ? entry.getXiaoshidanjia() : 1);

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
		order.setBencitingcheshizhang(chargeableHours);
		order.setBencitingchefeiyong(fee);
		order.setCrossrefid(entry.getId());
		order.setCheweiId(entry.getCheweiId());
		order.setIspay("未支付");

		tingchejiaofeiService.insert(order);
		// 这是N7代码 — 已支付补缴金额并入离场主单
		double totalFee = tingcheBujiaoN7Service.mergePaidIntoLichangOrder(body.getChezijinchangId(), order.getId(), fee);
		if (Math.abs(totalFee - fee) > 1e-6) {
			order.setBencitingchefeiyong(totalFee);
			tingchejiaofeiService.updateById(order);
		}
		try {
			cheweiZhuangtaiN2Service.afterTingchejiaofeiInserted(order);
		} catch (IllegalStateException ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return R.error(ex.getMessage());
		}
		Map<String, Object> payload = new HashMap<String, Object>(4);
		payload.put("order", order);
		payload.put("baseParkingFee", fee);
		payload.put("bujiaoMerged", totalFee - fee);
		payload.put("totalFee", totalFee);
		payload.put("jifeiCalc", calc);
		return R.ok().put("data", payload);
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
		EntityWrapper<CheweiYuyueEntity> yw = new EntityWrapper<CheweiYuyueEntity>();
		yw.eq("chezijinchang_id", chezijinchangId);
		yw.orderBy("id", false);
		List<CheweiYuyueEntity> ylist = cheweiYuyueDao.selectList(yw);
		data.put("yuyueList", ylist);
		if (ylist != null && !ylist.isEmpty()) {
			data.put("yuyue", ylist.get(0));
		}
		data.put("bujiaoList", tingcheBujiaoN7Service.listByChezijinchang(chezijinchangId).get("data"));
		return R.ok().put("data", data);
	}
}
