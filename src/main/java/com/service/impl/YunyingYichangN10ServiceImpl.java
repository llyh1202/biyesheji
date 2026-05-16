package com.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.constant.CheweiYuyueZhuangtaiN4;
import com.constant.N10YichangLeixing;
import com.constant.TingcheBujiaoZhuangtaiN7;
import com.constant.YuyueLiuchengJiedianM1;
import com.dao.CheweiYuyueDao;
import com.dao.TingcheBujiaoDao;
import com.entity.CheweiChaoshiGuizeEntity;
import com.entity.CheweiYuyueEntity;
import com.entity.ChezijinchangEntity;
import com.entity.TingcheBujiaoEntity;
import com.entity.TingchejiaofeiEntity;
import com.entity.dto.N10YichangReportDto;
import com.entity.dto.N10YichangRowDto;
import com.service.CheweiChaoshiN6Service;
import com.service.ChezijinchangService;
import com.service.TingchejiaofeiService;
import com.service.YunyingYichangN10Service;
import com.utils.R;

/**
 * 这是N10代码 — 异常报表（未入场预约、未支付离场、待补缴、超时未续费、N6 违约待付）。
 * 这是我cursor给父亲写的
 */
@Service("yunyingYichangN10Service")
public class YunyingYichangN10ServiceImpl implements YunyingYichangN10Service {

	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private CheweiYuyueDao cheweiYuyueDao;
	@Autowired
	private ChezijinchangService chezijinchangService;
	@Autowired
	private TingchejiaofeiService tingchejiaofeiService;
	@Autowired
	private TingcheBujiaoDao tingcheBujiaoDao;
	@Autowired
	private CheweiChaoshiN6Service cheweiChaoshiN6Service;

	@Override
	public R report() {
		N10YichangReportDto dto = new N10YichangReportDto();
		dto.setSnapshotAt(SDF.format(new Date()));
		Date now = new Date();

		List<N10YichangRowDto> weiRuchang = listWeiRuchangYuyue(now);
		List<N10YichangRowDto> weiZhifu = listWeiZhifuLichang();
		List<N10YichangRowDto> daiBujiao = listDaiBujiao();
		List<N10YichangRowDto> chaoshiXufei = listChaoshiWeiXufei(now);
		List<N10YichangRowDto> weiyue = listWeiyueWeiRuchang();

		dto.setWeiRuchangList(weiRuchang);
		dto.setWeiZhifuLichangList(weiZhifu);
		dto.setDaiBujiaoList(daiBujiao);
		dto.setChaoshiWeiXufeiList(chaoshiXufei);
		dto.setWeiyueWeiRuchangList(weiyue);
		dto.setWeiRuchangCount(weiRuchang.size());
		dto.setWeiZhifuLichangCount(weiZhifu.size());
		dto.setDaiBujiaoCount(daiBujiao.size());
		dto.setChaoshiWeiXufeiCount(chaoshiXufei.size());
		dto.setWeiyueWeiRuchangCount(weiyue.size());
		return R.ok().put("data", dto);
	}

	/** 这是N10代码 — 有效预约且未入场（与 N6 扫描对象一致） */
	private List<N10YichangRowDto> listWeiRuchangYuyue(Date now) {
		EntityWrapper<CheweiYuyueEntity> w = new EntityWrapper<CheweiYuyueEntity>();
		w.eq("zhuangtai", CheweiYuyueZhuangtaiN4.YOUXIAO);
		w.isNull("chezijinchang_id");
		w.orderBy("kaishi_shijian", false);
		List<N10YichangRowDto> rows = new ArrayList<N10YichangRowDto>();
		List<CheweiYuyueEntity> list = cheweiYuyueDao.selectList(w);
		for (CheweiYuyueEntity y : list) {
			String lj = nz(y.getLiuchengJiedian());
			if (StringUtils.isNotBlank(lj) && !YuyueLiuchengJiedianM1.YIYUYUE_DAIRUCHANG.equals(lj)) {
				continue;
			}
			N10YichangRowDto row = new N10YichangRowDto();
			row.setYichangLeixing(N10YichangLeixing.WEI_RUCHANG_YUYUE);
			row.setYichangLabel("未入场预约");
			row.setBizId(y.getId());
			row.setYuyueId(y.getId());
			row.setTingchechangmingcheng(y.getTingchechangmingcheng());
			row.setQuyu(y.getQuyu());
			row.setShijian(fmt(y.getKaishiShijian()));
			row.setZhuangtai(y.getZhuangtai());
			row.setLiuchengJiedian(lj);
			boolean overdue = isOverdueNoEntry(y, now);
			if (overdue) {
				row.setChuliJianyi("已过保留入场时限，可执行 N6 超时扫描或联系用户；若配置违约金将生成待付单");
			} else {
				row.setChuliJianyi("待用户按预约时段入场，或管理员协助 M2 入场");
			}
			rows.add(row);
		}
		return rows;
	}

	private boolean isOverdueNoEntry(CheweiYuyueEntity y, Date now) {
		if (y.getKaishiShijian() == null) {
			return false;
		}
		CheweiChaoshiGuizeEntity rule = cheweiChaoshiN6Service.resolveRule(y.getTingchechangmingcheng(), y.getQuyu());
		int hold = 30;
		if (rule != null && rule.getYuyueBaoliuFenzhong() != null) {
			hold = rule.getYuyueBaoliuFenzhong();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(y.getKaishiShijian());
		cal.add(Calendar.MINUTE, hold);
		return now.after(cal.getTime());
	}

	/** 这是N10代码 — 已离场未支付停车费 */
	private List<N10YichangRowDto> listWeiZhifuLichang() {
		EntityWrapper<TingchejiaofeiEntity> w = new EntityWrapper<TingchejiaofeiEntity>();
		w.isNotNull("lichangshijian");
		w.andNew().isNull("ispay").or().ne("ispay", "已支付");
		w.orderBy("lichangshijian", false);
		List<N10YichangRowDto> rows = new ArrayList<N10YichangRowDto>();
		for (TingchejiaofeiEntity o : tingchejiaofeiService.selectList(w)) {
			N10YichangRowDto row = new N10YichangRowDto();
			row.setYichangLeixing(N10YichangLeixing.WEI_ZHIFU_LICHANG);
			row.setYichangLabel("未支付离场");
			row.setBizId(o.getId());
			row.setTingchejiaofeiId(o.getId());
			row.setChezijinchangId(o.getCrossrefid());
			row.setTingchechangmingcheng(o.getTingchechangmingcheng());
			row.setQuyu(o.getQuyu());
			row.setYonghuzhanghao(o.getYonghuzhanghao());
			row.setChepaihao(o.getChepaihao());
			row.setJine(o.getBencitingchefeiyong());
			row.setShijian(fmt(o.getLichangshijian()));
			row.setZhuangtai(o.getIspay());
			row.setChuliJianyi("督促用户支付或管理端标记已支付（M3 关单）");
			rows.add(row);
		}
		return rows;
	}

	/** 这是N10代码 — N7 待支付补缴 */
	private List<N10YichangRowDto> listDaiBujiao() {
		EntityWrapper<TingcheBujiaoEntity> w = new EntityWrapper<TingcheBujiaoEntity>();
		w.eq("zhuangtai", TingcheBujiaoZhuangtaiN7.DAI_ZHIFU);
		w.orderBy("id", false);
		List<N10YichangRowDto> rows = new ArrayList<N10YichangRowDto>();
		for (TingcheBujiaoEntity b : tingcheBujiaoDao.selectList(w)) {
			N10YichangRowDto row = new N10YichangRowDto();
			row.setYichangLeixing(N10YichangLeixing.DAI_BUJIAO);
			row.setYichangLabel("待支付补缴");
			row.setBizId(b.getId());
			row.setChezijinchangId(b.getChezijinchangId());
			row.setYonghuzhanghao(b.getYonghuzhanghao());
			row.setChepaihao(b.getChepaihao());
			row.setJine(b.getJine());
			row.setShijian(fmt(b.getAddtime()));
			row.setZhuangtai(b.getZhuangtai());
			row.setLiuchengJiedian(b.getLeixing());
			row.setChuliJianyi("引导用户至 N7 补缴页支付，或管理员作废/调整");
			if (b.getChezijinchangId() != null) {
				ChezijinchangEntity e = chezijinchangService.selectById(b.getChezijinchangId());
				if (e != null) {
					row.setTingchechangmingcheng(e.getTingchechangmingcheng());
					row.setQuyu(e.getQuyu());
				}
			}
			rows.add(row);
		}
		return rows;
	}

	/** 这是N10代码 — 超时未续费：已入场、超过预约结束、无已支付超时补缴（N7） */
	private List<N10YichangRowDto> listChaoshiWeiXufei(Date now) {
		EntityWrapper<CheweiYuyueEntity> w = new EntityWrapper<CheweiYuyueEntity>();
		w.eq("zhuangtai", CheweiYuyueZhuangtaiN4.YOUXIAO);
		w.isNotNull("chezijinchang_id");
		w.isNotNull("jieshu_shijian");
		w.lt("jieshu_shijian", now);
		w.orderBy("jieshu_shijian", false);
		List<N10YichangRowDto> rows = new ArrayList<N10YichangRowDto>();
		Set<Long> handledEntry = new HashSet<Long>();
		for (CheweiYuyueEntity y : cheweiYuyueDao.selectList(w)) {
			if (y.getChezijinchangId() == null || handledEntry.contains(y.getChezijinchangId())) {
				continue;
			}
			String lj = nz(y.getLiuchengJiedian());
			if (!YuyueLiuchengJiedianM1.YIRUCHANG.equals(lj) && StringUtils.isNotBlank(lj)
					&& !YuyueLiuchengJiedianM1.YIYUYUE_DAIRUCHANG.equals(lj)) {
				continue;
			}
			if (hasPaidOvertimeBujiao(y.getChezijinchangId())) {
				continue;
			}
			handledEntry.add(y.getChezijinchangId());
			ChezijinchangEntity entry = chezijinchangService.selectById(y.getChezijinchangId());
			N10YichangRowDto row = new N10YichangRowDto();
			row.setYichangLeixing(N10YichangLeixing.CHAOSHI_WEI_XUFEI);
			row.setYichangLabel("超时未续费");
			row.setBizId(y.getId());
			row.setYuyueId(y.getId());
			row.setChezijinchangId(y.getChezijinchangId());
			row.setShijian(fmt(y.getJieshuShijian()));
			row.setZhuangtai(y.getZhuangtai());
			row.setLiuchengJiedian(lj);
			if (entry != null) {
				row.setTingchechangmingcheng(entry.getTingchechangmingcheng());
				row.setQuyu(entry.getQuyu());
				row.setChepaihao(entry.getChepaihao());
				row.setYonghuzhanghao(entry.getYonghuzhanghao());
			}
			boolean hasDaiBujiao = countDaiBujiaoByEntry(y.getChezijinchangId()) > 0;
			if (hasDaiBujiao) {
				row.setChuliJianyi("已有待支付补缴单，请跟进 N7 支付");
			} else {
				row.setChuliJianyi("在 N7 补缴管理执行「检测超时」并创建超时补缴单（M5 计费）");
			}
			rows.add(row);
		}
		return rows;
	}

	private boolean hasPaidOvertimeBujiao(Long chezijinchangId) {
		EntityWrapper<TingcheBujiaoEntity> w = new EntityWrapper<TingcheBujiaoEntity>();
		w.eq("chezijinchang_id", chezijinchangId);
		w.eq("leixing", "超时补缴");
		w.eq("zhuangtai", TingcheBujiaoZhuangtaiN7.YI_ZHIFU);
		return tingcheBujiaoDao.selectCount(w) > 0;
	}

	private int countDaiBujiaoByEntry(Long chezijinchangId) {
		EntityWrapper<TingcheBujiaoEntity> w = new EntityWrapper<TingcheBujiaoEntity>();
		w.eq("chezijinchang_id", chezijinchangId);
		w.eq("zhuangtai", TingcheBujiaoZhuangtaiN7.DAI_ZHIFU);
		return tingcheBujiaoDao.selectCount(w);
	}

	/** 这是N10代码 — N6 超时未入场违约金待付 */
	private List<N10YichangRowDto> listWeiyueWeiRuchang() {
		EntityWrapper<CheweiYuyueEntity> w = new EntityWrapper<CheweiYuyueEntity>();
		w.eq("liucheng_jiedian", YuyueLiuchengJiedianM1.CHAOSHI_WEIRUCHANG_DAIFU);
		w.orderBy("id", false);
		List<N10YichangRowDto> rows = new ArrayList<N10YichangRowDto>();
		for (CheweiYuyueEntity y : cheweiYuyueDao.selectList(w)) {
			if (y.getTingchejiaofeiId() == null) {
				continue;
			}
			TingchejiaofeiEntity order = tingchejiaofeiService.selectById(y.getTingchejiaofeiId());
			if (order == null || "已支付".equals(nz(order.getIspay()))) {
				continue;
			}
			N10YichangRowDto row = new N10YichangRowDto();
			row.setYichangLeixing(N10YichangLeixing.WEIYUE_WEI_RUCHANG);
			row.setYichangLabel("超时未入场违约");
			row.setBizId(y.getId());
			row.setYuyueId(y.getId());
			row.setTingchejiaofeiId(y.getTingchejiaofeiId());
			row.setTingchechangmingcheng(y.getTingchechangmingcheng());
			row.setQuyu(y.getQuyu());
			row.setJine(order.getBencitingchefeiyong());
			row.setShijian(fmt(y.getKaishiShijian()));
			row.setZhuangtai(y.getZhuangtai());
			row.setLiuchengJiedian(y.getLiuchengJiedian());
			row.setChuliJianyi("N6 已生成违约缴费单，督促支付或作废预约关联单");
			rows.add(row);
		}
		return rows;
	}

	private static String nz(String s) {
		return s == null ? "" : s.trim();
	}

	private static String fmt(Date d) {
		return d == null ? "" : SDF.format(d);
	}
}
