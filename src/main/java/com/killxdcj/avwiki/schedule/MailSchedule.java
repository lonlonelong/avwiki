package com.killxdcj.avwiki.schedule;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.killxdcj.avwiki.mail.AvwikiMailUtil;

@Component
public class MailSchedule {
	private static final Logger logger = Logger.getLogger(MailSchedule.class);

	@Scheduled(fixedDelay = 5 * 60 * 1000, initialDelay = 5 * 60 * 1000)
	public void asyncMailSchedule() {
		logger.info("MailSchedule.asyncMailSchedule START");
		AvwikiMailUtil.doSendAsyncMail();
		logger.info("MailSchedule.asyncMailSchedule END");
	}
}
