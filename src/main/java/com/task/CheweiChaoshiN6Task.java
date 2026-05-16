package com.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.service.CheweiChaoshiN6Service;
import com.utils.R;

/**
 * 这是N6代码 — 定时扫描预约超时未入场并自动取消/违约处理。
 * 这是我cursor给父亲写的
 */
@Component
public class CheweiChaoshiN6Task {

	private static final Logger log = LoggerFactory.getLogger(CheweiChaoshiN6Task.class);

	@Autowired
	private CheweiChaoshiN6Service cheweiChaoshiN6Service;

	@Value("${chewei.n6.scheduled-enabled:true}")
	private boolean scheduledEnabled;

	/** 每分钟执行一次 */
	@Scheduled(cron = "0 * * * * ?")
	public void scanYuyueTimeout() {
		if (!scheduledEnabled) {
			return;
		}
		try {
			R r = cheweiChaoshiN6Service.processScheduledTimeouts();
			if (r.get("data") != null) {
				log.debug("N6 timeout scan: {}", r.get("data"));
			}
		} catch (Exception ex) {
			log.warn("N6 timeout scan failed: {}", ex.getMessage());
		}
	}
}
