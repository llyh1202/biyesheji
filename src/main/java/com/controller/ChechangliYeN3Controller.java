package com.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annotation.IgnoreAuth;
import com.entity.ChezijinchangEntity;
import com.entity.dto.M2RuchangDto;
import com.entity.dto.N3TingcheJiesuanDto;
import com.entity.dto.N3TingcheLichangDto;
import com.service.ChechangliYeN3Service;
import com.utils.R;

/**
 * 这是N3代码 — 入场与离场业务单：编排入场、离场算费、结算关单（与预约/N2 车位状态衔接）。
 * 这是M2代码 — 预约校验入场快照与闭环入口 /m2/*。
 * 这是M3代码 — 结算关单与支付态一致，由 N2/M1 释放占用与完结预约。
 * 这是我cursor给父亲写的 — P1-13 M2 闭环写操作须登录并校验单据归属
 */
@RestController
@RequestMapping("/n3/tingcheli")
public class ChechangliYeN3Controller {

	@Autowired
	private ChechangliYeN3Service chechangliYeN3Service;

	private static String sessionUsername(HttpServletRequest request) {
		if (request == null || request.getSession() == null) {
			return "";
		}
		Object u = request.getSession().getAttribute("username");
		return u == null ? "" : u.toString();
	}

	/**
	 * 这是N3代码 — 入场业务：写入进场时间、车位，占用车位（N2）。
	 */
	@IgnoreAuth
	@RequestMapping("/ruchang")
	public R ruchang(@RequestBody ChezijinchangEntity entry) {
		return chechangliYeN3Service.ruchang(entry);
	}

	/** 这是M2代码 — 读预约单+车位+车场信息快照（不入库）。这是我cursor给父亲写的 — P1-13 须登录 */
	@RequestMapping("/m2/yuyue/snapshot")
	public R m2YuyueSnapshot(@RequestParam("yuyueId") Long yuyueId, HttpServletRequest request) {
		String user = sessionUsername(request);
		if (StringUtils.isBlank(user)) {
			return R.error(401, "请先登录");
		}
		return chechangliYeN3Service.m2YuyueSnapshot(yuyueId, user.trim());
	}

	/**
	 * 这是我cursor给父亲写的 — P1-13 校验预约归属后写入场单
	 */
	@RequestMapping("/m2/ruchang")
	public R m2Ruchang(@RequestBody M2RuchangDto body, HttpServletRequest request) {
		String user = sessionUsername(request);
		if (StringUtils.isBlank(user)) {
			return R.error(401, "请先登录");
		}
		return chechangliYeN3Service.m2RuchangByYuyue(body, user.trim());
	}

	/**
	 * 这是我cursor给父亲写的 — P1-13 校验入场单归属后生成离场缴费单
	 */
	@RequestMapping("/lichang")
	public R lichang(@RequestBody N3TingcheLichangDto body, HttpServletRequest request) {
		String user = sessionUsername(request);
		if (StringUtils.isBlank(user)) {
			return R.error(401, "请先登录");
		}
		return chechangliYeN3Service.lichang(body, user.trim());
	}

	/**
	 * 这是我cursor给父亲写的 — P1-13 校验缴费单归属后结算关单
	 */
	@RequestMapping("/jiesuan")
	public R jiesuan(@RequestBody N3TingcheJiesuanDto body, HttpServletRequest request) {
		String user = sessionUsername(request);
		if (StringUtils.isBlank(user)) {
			return R.error(401, "请先登录");
		}
		return chechangliYeN3Service.jiesuan(body, user.trim());
	}

	/** 这是我cursor给父亲写的 — P1-13 查询业务链须为本人入场单 */
	@RequestMapping("/chain")
	public R chain(@RequestParam("chezijinchangId") Long chezijinchangId, HttpServletRequest request) {
		String user = sessionUsername(request);
		if (StringUtils.isBlank(user)) {
			return R.error(401, "请先登录");
		}
		return chechangliYeN3Service.chainSummary(chezijinchangId, user.trim());
	}
}
