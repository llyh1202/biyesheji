package com.service;

import com.entity.ChezijinchangEntity;
import com.entity.dto.M2RuchangDto;
import com.entity.dto.N3TingcheJiesuanDto;
import com.entity.dto.N3TingcheLichangDto;
import com.utils.R;

/**
 * 这是N3代码 — 入场—离场—结算业务编排（与车位预约/状态机衔接，不单点缴费）。
 * 这是我cursor给父亲写的
 */
public interface ChechangliYeN3Service {

	/** 这是N3代码 — 入场单：写进场时间、车位，并触发 N2 占用车位 */
	R ruchang(ChezijinchangEntity entry);

	/** 这是M2代码 — 读预约、校验车位与时段后写入场并绑定该预约（闭环入口）。这是我cursor给父亲写的 — P1-13 须登录且校验预约归属 */
	R m2RuchangByYuyue(M2RuchangDto body, String sessionYonghuzhanghao);

	/** 这是M2代码 — 预约单+车位+车位信息快照，供前端展示与二次确认。这是我cursor给父亲写的 — P1-13 须登录且校验预约归属 */
	R m2YuyueSnapshot(Long yuyueId, String sessionYonghuzhanghao);

	/** 这是N3代码 — 离场单：由入场单生成缴费单（离场时间、时长、费用），须先有入场且车位为已入场。这是我cursor给父亲写的 — P1-13 须登录且校验入场单归属 */
	R lichang(N3TingcheLichangDto body, String sessionYonghuzhanghao);

	/** 这是N3代码 — 结算关单：标记已支付并触发 N2 释放车位。这是我cursor给父亲写的 — P1-13 须登录且校验缴费单归属 */
	R jiesuan(N3TingcheJiesuanDto body, String sessionYonghuzhanghao);

	/** 这是N3代码 — 入场单关联的缴费单与车位快照。这是我cursor给父亲写的 — P1-13 须登录且校验入场单归属 */
	R chainSummary(Long chezijinchangId, String sessionYonghuzhanghao);
}
