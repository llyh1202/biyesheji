package com.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annotation.IgnoreAuth;
import com.entity.dto.N7AdminBujiaoDto;
import com.entity.dto.N7PayDto;
import com.service.TingcheBujiaoN7Service;
import com.utils.R;

/**
 * 这是N7代码 — 续费/超时补缴接口。
 * 这是我cursor给父亲写的
 */
@RestController
@RequestMapping("/chewei/n7")
public class TingcheBujiaoN7Controller {

	@Autowired
	private TingcheBujiaoN7Service tingcheBujiaoN7Service;

	/** 管理员手工建补缴/调整单 */
	@RequestMapping("/bujiao/admin/create")
	public R adminCreate(@RequestBody N7AdminBujiaoDto body, HttpServletRequest request) {
		String admin = sessionUsername(request);
		return tingcheBujiaoN7Service.adminCreate(body, admin);
	}

	/** 用户补缴（模拟支付，与 M3 关单类似） */
	@IgnoreAuth
	@RequestMapping("/bujiao/pay")
	public R userPay(@RequestBody N7PayDto body, HttpServletRequest request) {
		Long id = body == null ? null : body.getId();
		return tingcheBujiaoN7Service.userPay(id, sessionUsername(request));
	}

	@RequestMapping("/bujiao/cancel")
	public R cancel(@RequestBody N7PayDto body, HttpServletRequest request) {
		Long id = body == null ? null : body.getId();
		return tingcheBujiaoN7Service.cancel(id, sessionUsername(request));
	}

	@IgnoreAuth
	@RequestMapping("/bujiao/list")
	public R list(@RequestParam(value = "chezijinchangId", required = false) Long chezijinchangId,
			@RequestParam(value = "yonghuzhanghao", required = false) String yonghuzhanghao, HttpServletRequest request) {
		if (chezijinchangId != null) {
			return tingcheBujiaoN7Service.listByChezijinchang(chezijinchangId);
		}
		String user = yonghuzhanghao;
		if (user == null || user.isEmpty()) {
			user = sessionUsername(request);
		}
		return tingcheBujiaoN7Service.listByUser(user);
	}

	@IgnoreAuth
	@RequestMapping("/overtime/check")
	public R overtimeCheck(@RequestParam("chezijinchangId") Long chezijinchangId) {
		return tingcheBujiaoN7Service.overtimeCheck(chezijinchangId);
	}

	private static String sessionUsername(HttpServletRequest request) {
		if (request == null || request.getSession() == null) {
			return "";
		}
		Object u = request.getSession().getAttribute("username");
		return u == null ? "" : u.toString();
	}
}
