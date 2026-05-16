package com.entity.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 这是N10代码 — 异常报表汇总。
 * 这是我cursor给父亲写的
 */
public class N10YichangReportDto {

	private String snapshotAt;
	private int weiRuchangCount;
	private int weiZhifuLichangCount;
	private int daiBujiaoCount;
	private int chaoshiWeiXufeiCount;
	private int weiyueWeiRuchangCount;
	private List<N10YichangRowDto> weiRuchangList = new ArrayList<N10YichangRowDto>();
	private List<N10YichangRowDto> weiZhifuLichangList = new ArrayList<N10YichangRowDto>();
	private List<N10YichangRowDto> daiBujiaoList = new ArrayList<N10YichangRowDto>();
	private List<N10YichangRowDto> chaoshiWeiXufeiList = new ArrayList<N10YichangRowDto>();
	private List<N10YichangRowDto> weiyueWeiRuchangList = new ArrayList<N10YichangRowDto>();

	public String getSnapshotAt() {
		return snapshotAt;
	}

	public void setSnapshotAt(String snapshotAt) {
		this.snapshotAt = snapshotAt;
	}

	public int getWeiRuchangCount() {
		return weiRuchangCount;
	}

	public void setWeiRuchangCount(int weiRuchangCount) {
		this.weiRuchangCount = weiRuchangCount;
	}

	public int getWeiZhifuLichangCount() {
		return weiZhifuLichangCount;
	}

	public void setWeiZhifuLichangCount(int weiZhifuLichangCount) {
		this.weiZhifuLichangCount = weiZhifuLichangCount;
	}

	public int getDaiBujiaoCount() {
		return daiBujiaoCount;
	}

	public void setDaiBujiaoCount(int daiBujiaoCount) {
		this.daiBujiaoCount = daiBujiaoCount;
	}

	public int getChaoshiWeiXufeiCount() {
		return chaoshiWeiXufeiCount;
	}

	public void setChaoshiWeiXufeiCount(int chaoshiWeiXufeiCount) {
		this.chaoshiWeiXufeiCount = chaoshiWeiXufeiCount;
	}

	public int getWeiyueWeiRuchangCount() {
		return weiyueWeiRuchangCount;
	}

	public void setWeiyueWeiRuchangCount(int weiyueWeiRuchangCount) {
		this.weiyueWeiRuchangCount = weiyueWeiRuchangCount;
	}

	public List<N10YichangRowDto> getWeiRuchangList() {
		return weiRuchangList;
	}

	public void setWeiRuchangList(List<N10YichangRowDto> weiRuchangList) {
		this.weiRuchangList = weiRuchangList;
	}

	public List<N10YichangRowDto> getWeiZhifuLichangList() {
		return weiZhifuLichangList;
	}

	public void setWeiZhifuLichangList(List<N10YichangRowDto> weiZhifuLichangList) {
		this.weiZhifuLichangList = weiZhifuLichangList;
	}

	public List<N10YichangRowDto> getDaiBujiaoList() {
		return daiBujiaoList;
	}

	public void setDaiBujiaoList(List<N10YichangRowDto> daiBujiaoList) {
		this.daiBujiaoList = daiBujiaoList;
	}

	public List<N10YichangRowDto> getChaoshiWeiXufeiList() {
		return chaoshiWeiXufeiList;
	}

	public void setChaoshiWeiXufeiList(List<N10YichangRowDto> chaoshiWeiXufeiList) {
		this.chaoshiWeiXufeiList = chaoshiWeiXufeiList;
	}

	public List<N10YichangRowDto> getWeiyueWeiRuchangList() {
		return weiyueWeiRuchangList;
	}

	public void setWeiyueWeiRuchangList(List<N10YichangRowDto> weiyueWeiRuchangList) {
		this.weiyueWeiRuchangList = weiyueWeiRuchangList;
	}
}
