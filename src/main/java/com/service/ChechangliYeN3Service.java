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

	/** 这是M2代码 — 读预约、校验车位与时段后写入场并绑定该预约（闭环入口）。 */
	R m2RuchangByYuyue(M2RuchangDto body);

	/** 这是M2代码 — 预约单+车位+车位信息快照，供前端展示与二次确认。 */
	R m2YuyueSnapshot(Long yuyueId);

	/** 这是N3代码 — 离场单：由入场单生成缴费单（离场时间、时长、费用），须先有入场且车位为已入场 */
	R lichang(N3TingcheLichangDto body);

	/** 这是N3代码 — 结算关单：标记已支付并触发 N2 释放车位 */
	R jiesuan(N3TingcheJiesuanDto body);

	/** 这是N3代码 — 入场单关联的缴费单与车位快照 */
	R chainSummary(Long chezijinchangId);
}
