package com.service.impl;

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
import com.constant.CheweiZhuangtaiN2;
import com.constant.TingcheBujiaoLeixingN7;
import com.constant.TingcheBujiaoZhuangtaiN7;
import com.constant.YuyueLiuchengJiedianM1;
import com.dao.CheweiYuyueDao;
import com.dao.TingcheBujiaoDao;
import com.entity.CheweiEntity;
import com.entity.CheweiYuyueEntity;
import com.entity.ChezijinchangEntity;
import com.entity.TingcheBujiaoEntity;
import com.entity.dto.N7AdminBujiaoDto;
import com.service.CheweiService;
import com.service.ChezijinchangService;
import com.service.TingcheBujiaoN7Service;
import com.utils.R;

/**
 * 这是N7代码 — 续费/超时补缴实现。
 * 这是我cursor给父亲写的
 */
@Service("tingcheBujiaoN7Service")
public class TingcheBujiaoN7ServiceImpl extends ServiceImpl<TingcheBujiaoDao, TingcheBujiaoEntity>
		implements TingcheBujiaoN7Service {

	@Autowired
	private ChezijinchangService chezijinchangService;
	@Autowired
	private CheweiService cheweiService;
	@Autowired
	private CheweiYuyueDao cheweiYuyueDao;

	private static String nz(String s) {
		return s == null ? "" : s.trim();
	}

	private static boolean allowedSpotStateForBujiao(String z) {
		return CheweiZhuangtaiN2.YI_RUCHANG.equals(z) || CheweiZhuangtaiN2.YI_LICHANG_DAI_JIESUAN.equals(z);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R adminCreate(N7AdminBujiaoDto body, String sessionAdminUsername) {
		if (body == null || body.getChezijinchangId() == null) {
			return R.error("须传入入场单 id：chezijinchangId");
		}
		if (body.getJine() == null || body.getJine() <= 0) {
			return R.error("补缴金额须大于 0");
		}
		ChezijinchangEntity entry = chezijinchangService.selectById(body.getChezijinchangId());
		if (entry == null) {
			return R.error("入场单不存在");
		}
		if (entry.getCheweiId() != null) {
			CheweiEntity cw = cheweiService.selectById(entry.getCheweiId());
			if (cw != null && !allowedSpotStateForBujiao(nz(cw.getZhuangtai()))) {
				return R.error("当前车位状态为「" + cw.getZhuangtai() + "」，仅「已入场」或「已离场待结算」可建补缴单");
			}
		}
		String leixing = nz(body.getLeixing());
		if (StringUtils.isBlank(leixing)) {
			leixing = TingcheBujiaoLeixingN7.GUANLI_TIAOZHENG;
		}
		TingcheBujiaoEntity row = new TingcheBujiaoEntity();
		row.setDanhao("BJ" + System.currentTimeMillis());
		row.setLeixing(leixing);
		row.setChezijinchangId(entry.getId());
		row.setYonghuzhanghao(entry.getYonghuzhanghao());
		row.setXingming(entry.getXingming());
		row.setChepaihao(entry.getChepaihao());
		row.setJine(body.getJine());
		row.setYuanyin(body.getYuanyin());
		row.setBeizhu(body.getBeizhu());
		row.setZhuangtai(TingcheBujiaoZhuangtaiN7.DAI_ZHIFU);
		String admin = nz(body.getGuanliyuanZhanghao());
		if (StringUtils.isBlank(admin)) {
			admin = nz(sessionAdminUsername);
		}
		row.setGuanliyuanZhanghao(admin);
		row.setAddtime(new Date());
		insert(row);
		return R.ok().put("data", row);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R userPay(Long id, String sessionUsername) {
		if (id == null) {
			return R.error("须传入补缴单 id");
		}
		TingcheBujiaoEntity row = selectById(id);
		if (row == null) {
			return R.error("补缴单不存在");
		}
		if (!TingcheBujiaoZhuangtaiN7.DAI_ZHIFU.equals(nz(row.getZhuangtai()))) {
			return R.error("当前状态不可支付：" + row.getZhuangtai());
		}
		if (StringUtils.isNotBlank(sessionUsername) && StringUtils.isNotBlank(row.getYonghuzhanghao())
				&& !sessionUsername.equals(row.getYonghuzhanghao())) {
			return R.error("只能支付本人账号下的补缴单");
		}
		row.setZhuangtai(TingcheBujiaoZhuangtaiN7.YI_ZHIFU);
		row.setZhifuShijian(new Date());
		updateById(row);
		return R.ok().put("data", row);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R cancel(Long id, String sessionAdminUsername) {
		if (id == null) {
			return R.error("须传入补缴单 id");
		}
		TingcheBujiaoEntity row = selectById(id);
		if (row == null) {
			return R.error("补缴单不存在");
		}
		if (TingcheBujiaoZhuangtaiN7.YI_BINGRU_LICHANG.equals(nz(row.getZhuangtai()))) {
			return R.error("已并入离场单的补缴单不可作废");
		}
		row.setZhuangtai(TingcheBujiaoZhuangtaiN7.YI_ZUOFEI);
		row.setBeizhu((nz(row.getBeizhu()) + " [作废:" + nz(sessionAdminUsername) + "]").trim());
		updateById(row);
		return R.ok();
	}

	@Override
	public R listByChezijinchang(Long chezijinchangId) {
		if (chezijinchangId == null) {
			return R.error("须传入 chezijinchangId");
		}
		EntityWrapper<TingcheBujiaoEntity> w = new EntityWrapper<TingcheBujiaoEntity>();
		w.eq("chezijinchang_id", chezijinchangId);
		w.orderBy("id", false);
		return R.ok().put("data", selectList(w));
	}

	@Override
	public R listByUser(String yonghuzhanghao) {
		EntityWrapper<TingcheBujiaoEntity> w = new EntityWrapper<TingcheBujiaoEntity>();
		if (StringUtils.isNotBlank(yonghuzhanghao)) {
			w.eq("yonghuzhanghao", yonghuzhanghao);
		}
		w.ne("zhuangtai", TingcheBujiaoZhuangtaiN7.YI_ZUOFEI);
		w.orderBy("id", false);
		return R.ok().put("data", selectList(w));
	}

	@Override
	public R overtimeCheck(Long chezijinchangId) {
		if (chezijinchangId == null) {
			return R.error("须传入 chezijinchangId");
		}
		ChezijinchangEntity entry = chezijinchangService.selectById(chezijinchangId);
		if (entry == null) {
			return R.error("入场单不存在");
		}
		Map<String, Object> data = new HashMap<String, Object>(8);
		data.put("chezijinchangId", chezijinchangId);
		data.put("overtime", false);
		data.put("suggestedJine", 0.0);
		data.put("hint", "未关联有效预约时段，可由管理员手工建补缴单。");
		Date now = new Date();
		EntityWrapper<CheweiYuyueEntity> yw = new EntityWrapper<CheweiYuyueEntity>();
		yw.eq("chezijinchang_id", chezijinchangId);
		yw.orderBy("id", false);
		List<CheweiYuyueEntity> ylist = cheweiYuyueDao.selectList(yw);
		if (ylist == null || ylist.isEmpty()) {
			return R.ok().put("data", data);
		}
		CheweiYuyueEntity y = ylist.get(0);
		data.put("yuyue", y);
		if (y.getJieshuShijian() != null && now.after(y.getJieshuShijian())) {
			long ms = now.getTime() - y.getJieshuShijian().getTime();
			double overHours = Math.ceil(ms / (1000.0 * 3600.0));
			int danjia = entry.getXiaoshidanjia() != null && entry.getXiaoshidanjia() > 0 ? entry.getXiaoshidanjia() : 1;
			double suggested = overHours * danjia;
			data.put("overtime", true);
			data.put("overHours", overHours);
			data.put("suggestedJine", suggested);
			data.put("hint", "已超过预约结束时间，建议补缴约 " + suggested + " 元（按超时时长向上取整小时 × 单价）");
			String lj = nz(y.getLiuchengJiedian());
			if (YuyueLiuchengJiedianM1.YIRUCHANG.equals(lj) || StringUtils.isBlank(lj)) {
				data.put("canCreateBujiao", true);
			}
		}
		return R.ok().put("data", data);
	}

	@Override
	public void assertNoUnpaidBeforeLichang(Long chezijinchangId) {
		EntityWrapper<TingcheBujiaoEntity> w = new EntityWrapper<TingcheBujiaoEntity>();
		w.eq("chezijinchang_id", chezijinchangId);
		w.eq("zhuangtai", TingcheBujiaoZhuangtaiN7.DAI_ZHIFU);
		int n = selectCount(w);
		if (n > 0) {
			throw new IllegalStateException("存在 " + n + " 笔待支付补缴单，请先完成用户补缴或由管理员作废后再离场结算");
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public double mergePaidIntoLichangOrder(Long chezijinchangId, Long tingchejiaofeiId, double baseFee) {
		EntityWrapper<TingcheBujiaoEntity> w = new EntityWrapper<TingcheBujiaoEntity>();
		w.eq("chezijinchang_id", chezijinchangId);
		w.eq("zhuangtai", TingcheBujiaoZhuangtaiN7.YI_ZHIFU);
		List<TingcheBujiaoEntity> paid = selectList(w);
		double extra = 0.0;
		Date now = new Date();
		for (TingcheBujiaoEntity b : paid) {
			if (b.getJine() != null) {
				extra += b.getJine();
			}
			b.setZhuangtai(TingcheBujiaoZhuangtaiN7.YI_BINGRU_LICHANG);
			b.setTingchejiaofeiId(tingchejiaofeiId);
			if (b.getZhifuShijian() == null) {
				b.setZhifuShijian(now);
			}
			updateById(b);
		}
		return baseFee + extra;
	}
}
