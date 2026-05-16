package com.controller;

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
 * 这是我cursor给父亲写的
 */
@RestController
@RequestMapping("/n3/tingcheli")
public class ChechangliYeN3Controller {

	@Autowired
	private ChechangliYeN3Service chechangliYeN3Service;

	/**
	 * 这是N3代码 — 入场业务：写入进场时间、车位，占用车位（N2）。
	 */
	@IgnoreAuth
	@RequestMapping("/ruchang")
	public R ruchang(@RequestBody ChezijinchangEntity entry) {
		return chechangliYeN3Service.ruchang(entry);
	}

	/** 这是M2代码 — 读预约单+车位+车场信息快照（不入库）。 */
	@IgnoreAuth
	@RequestMapping("/m2/yuyue/snapshot")
	public R m2YuyueSnapshot(@RequestParam("yuyueId") Long yuyueId) {
		return chechangliYeN3Service.m2YuyueSnapshot(yuyueId);
	}

	/**
	 * 这是M2代码 — 校验预约与时段后写入场单，并显式绑定该预约（推荐前台主入口）。
	 */
	@IgnoreAuth
	@RequestMapping("/m2/ruchang")
	public R m2Ruchang(@RequestBody M2RuchangDto body) {
		return chechangliYeN3Service.m2RuchangByYuyue(body);
	}

	/**
	 * 这是N3代码 — 离场业务：根据入场单生成缴费单（离场时间、时长、费用），进入待结算（N2）。这是M2代码 — body 可带
	 * yuyueId 与入场单一并校验。
	 */
	@IgnoreAuth
	@RequestMapping("/lichang")
	public R lichang(@RequestBody N3TingcheLichangDto body) {
		return chechangliYeN3Service.lichang(body);
	}

	/**
	 * 这是N3代码 — 结算关单：标记已支付并释放车位占用（N2）。
	 */
	@IgnoreAuth
	@RequestMapping("/jiesuan")
	public R jiesuan(@RequestBody N3TingcheJiesuanDto body) {
		return chechangliYeN3Service.jiesuan(body);
	}

	/** 这是N3代码 — 查询某入场单下的车位与关联缴费单（业务链核对）。 */
	@IgnoreAuth
	@RequestMapping("/chain")
	public R chain(@RequestParam("chezijinchangId") Long chezijinchangId) {
		return chechangliYeN3Service.chainSummary(chezijinchangId);
	}
}
