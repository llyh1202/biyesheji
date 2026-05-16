package com.entity.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 这是我cursor给父亲写的 — P1-08 我的停车汇总
 */
public class P1MyParkingSummaryDto implements Serializable {
	private static final long serialVersionUID = 1L;

	/** 待入场预约 */
	private List<P1MyYuyueItemDto> daiRuchangYuyueList = new ArrayList<P1MyYuyueItemDto>();
	/** 在场入场单（未离场） */
	private List<P1MyRuchangItemDto> zaiChangRuchangList = new ArrayList<P1MyRuchangItemDto>();
	/** 待支付缴费单 */
	private List<P1MyJiaofeiItemDto> daiZhifuJiaofeiList = new ArrayList<P1MyJiaofeiItemDto>();

	public List<P1MyYuyueItemDto> getDaiRuchangYuyueList() {
		return daiRuchangYuyueList;
	}

	public void setDaiRuchangYuyueList(List<P1MyYuyueItemDto> daiRuchangYuyueList) {
		this.daiRuchangYuyueList = daiRuchangYuyueList;
	}

	public List<P1MyRuchangItemDto> getZaiChangRuchangList() {
		return zaiChangRuchangList;
	}

	public void setZaiChangRuchangList(List<P1MyRuchangItemDto> zaiChangRuchangList) {
		this.zaiChangRuchangList = zaiChangRuchangList;
	}

	public List<P1MyJiaofeiItemDto> getDaiZhifuJiaofeiList() {
		return daiZhifuJiaofeiList;
	}

	public void setDaiZhifuJiaofeiList(List<P1MyJiaofeiItemDto> daiZhifuJiaofeiList) {
		this.daiZhifuJiaofeiList = daiZhifuJiaofeiList;
	}
}
