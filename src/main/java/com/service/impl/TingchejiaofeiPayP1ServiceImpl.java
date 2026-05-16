package com.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.dao.CheweiYuyueDao;
import com.entity.ChezijinchangEntity;
import com.entity.CheweiYuyueEntity;
import com.entity.TingchejiaofeiEntity;
import com.service.CheweiZhuangtaiN2Service;
import com.service.ChezijinchangService;
import com.service.TingchejiaofeiPayP1Service;
import com.service.TingchejiaofeiService;
import com.utils.R;

/**
 * 这是我cursor给父亲写的 — P1-19 停车费 payComplete 实现
 */
@Service("tingchejiaofeiPayP1Service")
public class TingchejiaofeiPayP1ServiceImpl implements TingchejiaofeiPayP1Service {

	@Autowired
	private TingchejiaofeiService tingchejiaofeiService;
	@Autowired
	private ChezijinchangService chezijinchangService;
	@Autowired
	private CheweiZhuangtaiN2Service cheweiZhuangtaiN2Service;
	@Autowired
	private CheweiYuyueDao cheweiYuyueDao;

	private static String nz(String s) {
		if (StringUtils.isBlank(s)) {
			return "";
		}
		return s.trim();
	}

	private R requireLogin(String sessionYonghuzhanghao) {
		if (StringUtils.isBlank(sessionYonghuzhanghao)) {
			return R.error(401, "请先登录");
		}
		return null;
	}

	private void patchTingchejiaofeiYonghu(Long id, String yonghuzhanghao) {
		if (id == null || StringUtils.isBlank(yonghuzhanghao)) {
			return;
		}
		TingchejiaofeiEntity patch = new TingchejiaofeiEntity();
		patch.setId(id);
		patch.setYonghuzhanghao(yonghuzhanghao.trim());
		tingchejiaofeiService.updateById(patch);
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

	/** 这是我cursor给父亲写的 — P1-19 与 N3 一致的缴费单归属校验 */
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
				String eu = nz(entry.getYonghuzhanghao());
				if (StringUtils.isNotBlank(eu)) {
					if (!session.equals(eu)) {
						return R.error(403, "无权操作他人的缴费单");
					}
					patchTingchejiaofeiYonghu(order.getId(), eu);
					order.setYonghuzhanghao(eu);
					return null;
				}
				EntityWrapper<CheweiYuyueEntity> yw = new EntityWrapper<CheweiYuyueEntity>();
				yw.eq("chezijinchang_id", entry.getId());
				yw.last("LIMIT 1");
				CheweiYuyueEntity y = cheweiYuyueDao.selectOne(yw);
				if (y != null && StringUtils.isNotBlank(y.getYonghuzhanghao())) {
					String yu = nz(y.getYonghuzhanghao());
					if (!session.equals(yu)) {
						return R.error(403, "无权操作他人的缴费单");
					}
					patchTingchejiaofeiYonghu(order.getId(), yu);
					order.setYonghuzhanghao(yu);
					return null;
				}
			}
		}
		patchTingchejiaofeiYonghu(order.getId(), session);
		patchChezijinchangYonghu(order.getCrossrefid(), session);
		order.setYonghuzhanghao(session);
		return null;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R payComplete(Long tingchejiaofeiId, String sessionYonghuzhanghao) {
		if (tingchejiaofeiId == null) {
			return R.error("须传入缴费单 id：tingchejiaofeiId");
		}
		TingchejiaofeiEntity current = tingchejiaofeiService.selectById(tingchejiaofeiId);
		if (current == null) {
			return R.error("缴费单不存在");
		}
		R ownerCheck = assertTingchejiaofeiOwner(sessionYonghuzhanghao, current);
		if (ownerCheck != null) {
			return ownerCheck;
		}
		if (current.getLichangshijian() == null) {
			return R.error("须先完成离场记录（含离场时间）后再支付，请使用 M2「生成缴费单」");
		}
		if ("已支付".equals(nz(current.getIspay()))) {
			return R.ok().put("data", current).put("msg", "订单已是已支付");
		}
		TingchejiaofeiEntity before = new TingchejiaofeiEntity();
		before.setId(current.getId());
		before.setIspay(current.getIspay());

		current.setIspay("已支付");
		tingchejiaofeiService.updateById(current);
		TingchejiaofeiEntity after = tingchejiaofeiService.selectById(tingchejiaofeiId);
		try {
			cheweiZhuangtaiN2Service.afterTingchejiaofeiUpdatedIfPaid(before, after);
		} catch (IllegalStateException ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return R.error(ex.getMessage());
		}
		return R.ok().put("data", after);
	}
}
