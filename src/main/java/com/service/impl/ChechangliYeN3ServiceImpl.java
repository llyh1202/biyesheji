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
import com.entity.YonghuEntity;
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
import com.service.YonghuService;
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
	@Autowired
	private YonghuService yonghuService;

	private static String nz(String s) {
		if (StringUtils.isBlank(s)) {
			return "";
		}
		return s.trim();
	}

	/** 这是我cursor给父亲写的 — P1-13 登录用户校验 */
	private R requireLogin(String sessionYonghuzhanghao) {
		if (StringUtils.isBlank(sessionYonghuzhanghao)) {
			return R.error(401, "请先登录");
		}
		return null;
	}

	/** 这是我cursor给父亲写的 — P1-13 业务单 yonghuzhanghao 须与当前用户一致（已绑定账号时） */
	private R assertYonghuOwner(String sessionYonghuzhanghao, String entityYonghuzhanghao, String bizLabel) {
		R login = requireLogin(sessionYonghuzhanghao);
		if (login != null) {
			return login;
		}
		String session = sessionYonghuzhanghao.trim();
		String owner = nz(entityYonghuzhanghao);
		if (StringUtils.isBlank(owner)) {
			return R.error("该" + bizLabel + "未绑定用户账号，无法操作");
		}
		if (!session.equals(owner)) {
			return R.error(403, "无权操作他人的" + bizLabel);
		}
		return null;
	}

	private void patchYuyueYonghu(Long yuyueId, String yonghuzhanghao) {
		if (yuyueId == null || StringUtils.isBlank(yonghuzhanghao)) {
			return;
		}
		CheweiYuyueEntity patch = new CheweiYuyueEntity();
		patch.setId(yuyueId);
		patch.setYonghuzhanghao(yonghuzhanghao.trim());
		cheweiYuyueDao.updateById(patch);
	}

	private void patchChezijinchangYonghu(Long chezijinchangId, String yonghuzhanghao) {
		if (chezijinchangId == null || StringUtils.isBlank(yonghuzhanghao)) {
			return;
		}
		ChezijinchangEntity patch = new ChezijinchangEntity();
		patch.setId(chezijinchangId);
		patch.setYonghuzhanghao(yonghuzhanghao.trim());
		chezijinchangService.updateById(patch);
	}

	private void patchTingchejiaofeiYonghu(Long tingchejiaofeiId, String yonghuzhanghao) {
		if (tingchejiaofeiId == null || StringUtils.isBlank(yonghuzhanghao)) {
			return;
		}
		TingchejiaofeiEntity patch = new TingchejiaofeiEntity();
		patch.setId(tingchejiaofeiId);
		patch.setYonghuzhanghao(yonghuzhanghao.trim());
		tingchejiaofeiService.updateById(patch);
	}

	/**
	 * 这是我cursor给父亲写的 — P1-13 历史预约：从入场单回填；仍为空且待入场时由当前用户认领
	 */
	private R assertYuyueOwner(String sessionYonghuzhanghao, CheweiYuyueEntity yuyue) {
		if (yuyue == null) {
			return R.error("预约单不存在");
		}
		R login = requireLogin(sessionYonghuzhanghao);
		if (login != null) {
			return login;
		}
		String session = sessionYonghuzhanghao.trim();
		String owner = nz(yuyue.getYonghuzhanghao());
		if (StringUtils.isNotBlank(owner)) {
			if (!session.equals(owner)) {
				return R.error(403, "无权操作他人的预约单");
			}
			return null;
		}
		if (yuyue.getChezijinchangId() != null) {
			ChezijinchangEntity entry = chezijinchangService.selectById(yuyue.getChezijinchangId());
			if (entry != null && StringUtils.isNotBlank(entry.getYonghuzhanghao())) {
				String eu = nz(entry.getYonghuzhanghao());
				if (!session.equals(eu)) {
					return R.error(403, "无权操作他人的预约单");
				}
				patchYuyueYonghu(yuyue.getId(), eu);
				yuyue.setYonghuzhanghao(eu);
				return null;
			}
		}
		String lj = nz(yuyue.getLiuchengJiedian());
		boolean daiRuchang = StringUtils.isBlank(lj) || YuyueLiuchengJiedianM1.YIYUYUE_DAIRUCHANG.equals(lj);
		if (CheweiYuyueZhuangtaiN4.YOUXIAO.equals(nz(yuyue.getZhuangtai())) && daiRuchang
				&& yuyue.getChezijinchangId() == null) {
			patchYuyueYonghu(yuyue.getId(), session);
			yuyue.setYonghuzhanghao(session);
			return null;
		}
		return R.error("该预约单未绑定用户账号，无法操作");
	}

	/** 这是我cursor给父亲写的 — P1-13 历史入场单：从预约/缴费单回填；入场中可由当前用户认领 */
	private R assertChezijinchangOwner(String sessionYonghuzhanghao, ChezijinchangEntity entry) {
		if (entry == null) {
			return R.error("入场单不存在");
		}
		R login = requireLogin(sessionYonghuzhanghao);
		if (login != null) {
			return login;
		}
		String session = sessionYonghuzhanghao.trim();
		String owner = nz(entry.getYonghuzhanghao());
		if (StringUtils.isNotBlank(owner)) {
			if (!session.equals(owner)) {
				return R.error(403, "无权操作他人的入场单");
			}
			return null;
		}
		EntityWrapper<CheweiYuyueEntity> yw = new EntityWrapper<CheweiYuyueEntity>();
		yw.eq("chezijinchang_id", entry.getId());
		yw.last("LIMIT 1");
		CheweiYuyueEntity y = cheweiYuyueDao.selectOne(yw);
		if (y != null && StringUtils.isNotBlank(y.getYonghuzhanghao())) {
			String yu = nz(y.getYonghuzhanghao());
			if (!session.equals(yu)) {
				return R.error(403, "无权操作他人的入场单");
			}
			patchChezijinchangYonghu(entry.getId(), yu);
			entry.setYonghuzhanghao(yu);
			return null;
		}
		EntityWrapper<TingchejiaofeiEntity> fw = new EntityWrapper<TingchejiaofeiEntity>();
		fw.eq("crossrefid", entry.getId());
		fw.last("LIMIT 1");
		List<TingchejiaofeiEntity> fees = tingchejiaofeiService.selectList(fw);
		if (fees != null && !fees.isEmpty() && StringUtils.isNotBlank(fees.get(0).getYonghuzhanghao())) {
			String fu = nz(fees.get(0).getYonghuzhanghao());
			if (!session.equals(fu)) {
				return R.error(403, "无权操作他人的入场单");
			}
			patchChezijinchangYonghu(entry.getId(), fu);
			entry.setYonghuzhanghao(fu);
			return null;
		}
		patchChezijinchangYonghu(entry.getId(), session);
		entry.setYonghuzhanghao(session);
		return null;
	}

	/** 这是我cursor给父亲写的 — P1-13 历史缴费单：从入场单回填 */
	private R assertTingchejiaofeiOwner(String sessionYonghuzhanghao, TingchejiaofeiEntity order) {
		if (order == null) {
			return R.error("缴费单不存在");
		}
		R login = requireLogin(sessionYonghuzhanghao);
		if (login != null) {
			return login;
		}
		String session = sessionYonghuzhanghao.trim();
		String owner = nz(order.getYonghuzhanghao());
		if (StringUtils.isNotBlank(owner)) {
			if (!session.equals(owner)) {
				return R.error(403, "无权操作他人的缴费单");
			}
			return null;
		}
		if (order.getCrossrefid() != null) {
			ChezijinchangEntity entry = chezijinchangService.selectById(order.getCrossrefid());
			if (entry != null) {
				R cr = assertChezijinchangOwner(session, entry);
				if (cr != null) {
					return cr;
				}
				if (StringUtils.isNotBlank(entry.getYonghuzhanghao())) {
					patchTingchejiaofeiYonghu(order.getId(), entry.getYonghuzhanghao());
					order.setYonghuzhanghao(entry.getYonghuzhanghao());
					return null;
				}
			}
		}
		patchTingchejiaofeiYonghu(order.getId(), session);
		order.setYonghuzhanghao(session);
		return null;
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
	public R m2YuyueSnapshot(Long yuyueId, String sessionYonghuzhanghao) {
		if (yuyueId == null) {
			return R.error("须传入预约单 id：yuyueId");
		}
		CheweiYuyueEntity yuyue = cheweiYuyueDao.selectById(yuyueId);
		R ownerCheck = assertYuyueOwner(sessionYonghuzhanghao, yuyue);
		if (ownerCheck != null) {
			return ownerCheck;
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

	/**
	 * 这是我cursor给父亲写的 — P1-14：请求体缺省时从 session/用户表补全；车牌优先 yonghu.chepaihao
	 */
	private void fillM2RuchangFromSession(M2RuchangDto body, String sessionUser) {
		if (body == null || StringUtils.isBlank(sessionUser)) {
			return;
		}
		String session = sessionUser.trim();
		if (StringUtils.isBlank(body.getYonghuzhanghao())) {
			body.setYonghuzhanghao(session);
		}
		EntityWrapper<YonghuEntity> uw = new EntityWrapper<YonghuEntity>();
		uw.eq("yonghuzhanghao", session);
		YonghuEntity yonghu = yonghuService.selectOne(uw);
		if (yonghu == null) {
			return;
		}
		if (StringUtils.isBlank(body.getXingming()) && StringUtils.isNotBlank(yonghu.getXingming())) {
			body.setXingming(yonghu.getXingming().trim());
		}
		if (StringUtils.isBlank(body.getShouji()) && StringUtils.isNotBlank(yonghu.getShouji())) {
			body.setShouji(yonghu.getShouji().trim());
		}
		if (StringUtils.isNotBlank(yonghu.getChepaihao())) {
			body.setChepaihao(yonghu.getChepaihao().trim());
		} else if (StringUtils.isNotBlank(body.getChepaihao())) {
			body.setChepaihao(body.getChepaihao().trim());
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R m2RuchangByYuyue(M2RuchangDto body, String sessionYonghuzhanghao) {
		R login = requireLogin(sessionYonghuzhanghao);
		if (login != null) {
			return login;
		}
		String sessionUser = sessionYonghuzhanghao.trim();
		if (body == null || body.getYuyueId() == null) {
			return R.error("须传入预约单 id：yuyueId");
		}
		fillM2RuchangFromSession(body, sessionUser);
		if (StringUtils.isBlank(body.getChepaihao())) {
			return R.error("须填写车牌号 chepaihao（可在个人资料中维护车牌，或入场时传入）");
		}
		CheweiYuyueEntity yuyue = cheweiYuyueDao.selectById(body.getYuyueId());
		R ownerCheck = assertYuyueOwner(sessionUser, yuyue);
		if (ownerCheck != null) {
			return ownerCheck;
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
		// 这是我cursor给父亲写的 — P1-13/P1-14 用户信息与 session、用户表补全后写入入场单
		entry.setYonghuzhanghao(sessionUser);
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
	public R lichang(N3TingcheLichangDto body, String sessionYonghuzhanghao) {
		if (body == null || body.getChezijinchangId() == null) {
			return R.error("须传入入场单 id：chezijinchangId");
		}
		ChezijinchangEntity entry = chezijinchangService.selectById(body.getChezijinchangId());
		if (entry == null) {
			return R.error("入场单不存在");
		}
		R ownerCheck = assertChezijinchangOwner(sessionYonghuzhanghao, entry);
		if (ownerCheck != null) {
			return ownerCheck;
		}
		if (entry.getCheweiId() == null) {
			return R.error("入场单未关联车位，无法生成离场业务单");
		}
		if (entry.getJinchangshijian() == null) {
			return R.error("入场单缺少进场时间，无法计费离场");
		}
		if (body.getYuyueId() != null) {
			CheweiYuyueEntity y = cheweiYuyueDao.selectById(body.getYuyueId());
			R yuyueOwner = assertYuyueOwner(sessionYonghuzhanghao, y);
			if (yuyueOwner != null) {
				return yuyueOwner;
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
	public R jiesuan(N3TingcheJiesuanDto body, String sessionYonghuzhanghao) {
		if (body == null || body.getTingchejiaofeiId() == null) {
			return R.error("须传入缴费单 id：tingchejiaofeiId");
		}
		Long id = body.getTingchejiaofeiId();
		TingchejiaofeiEntity current = tingchejiaofeiService.selectById(id);
		if (current == null) {
			return R.error("缴费单不存在");
		}
		R ownerCheck = assertTingchejiaofeiOwner(sessionYonghuzhanghao, current);
		if (ownerCheck != null) {
			return ownerCheck;
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
	public R chainSummary(Long chezijinchangId, String sessionYonghuzhanghao) {
		if (chezijinchangId == null) {
			return R.error("须传入 chezijinchangId");
		}
		ChezijinchangEntity entry = chezijinchangService.selectById(chezijinchangId);
		if (entry == null) {
			return R.error("入场单不存在");
		}
		R ownerCheck = assertChezijinchangOwner(sessionYonghuzhanghao, entry);
		if (ownerCheck != null) {
			return ownerCheck;
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
