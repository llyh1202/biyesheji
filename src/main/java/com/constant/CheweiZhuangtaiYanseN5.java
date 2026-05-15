package com.constant;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * 这是N5代码 — 车位状态与展示色（可视化色块）。
 * 这是我cursor给父亲写的
 */
public final class CheweiZhuangtaiYanseN5 {

	private CheweiZhuangtaiYanseN5() {
	}

	private static final Map<String, String> ZHUANGTAI_TO_HEX = new LinkedHashMap<String, String>();

	static {
		ZHUANGTAI_TO_HEX.put(CheweiZhuangtaiN2.KONGXIAN, "#67C23A");
		ZHUANGTAI_TO_HEX.put(CheweiZhuangtaiN2.YUYUE_WEIRUCHANG, "#E6A23C");
		ZHUANGTAI_TO_HEX.put(CheweiZhuangtaiN2.YI_RUCHANG, "#409EFF");
		ZHUANGTAI_TO_HEX.put(CheweiZhuangtaiN2.YI_LICHANG_DAI_JIESUAN, "#F56C6C");
		ZHUANGTAI_TO_HEX.put(CheweiZhuangtaiN2.YI_JIESUAN, "#909399");
	}

	public static String colorHexForZhuangtai(String zhuangtai) {
		if (StringUtils.isBlank(zhuangtai)) {
			return ZHUANGTAI_TO_HEX.get(CheweiZhuangtaiN2.KONGXIAN);
		}
		String z = zhuangtai.trim();
		String c = ZHUANGTAI_TO_HEX.get(z);
		return c != null ? c : "#C0C4CC";
	}

	public static Map<String, String> legendMap() {
		return new LinkedHashMap<String, String>(ZHUANGTAI_TO_HEX);
	}
}
