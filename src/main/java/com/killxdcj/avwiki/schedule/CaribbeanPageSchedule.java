package com.killxdcj.avwiki.schedule;

import java.lang.Thread.State;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.killxdcj.avwiki.caribbean.CaribbeanPageProcessor;
import com.killxdcj.avwiki.spider.Spider;

@Component
public class CaribbeanPageSchedule {
	private static final Logger logger = Logger.getLogger(CaribbeanPageSchedule.class);
	
	@Scheduled(cron="0 0 0 * * ?")//每天0点触发
	public void doSchedule() {
		logger.info("CaribbeanPageSchedule:START");
		
		Spider spider = new Spider("caribbeancom");
		spider.setPageProcessor(new CaribbeanPageProcessor());
		spider.addSeed("http://www.caribbeancompr.com/listpages/1.html");
		spider.addIncludeRegex("http://www.caribbeancompr.com/listpages/[0-9]+.html");
		spider.addIncludeRegex("http://www.caribbeancompr.com/moviepages/[0-9]+_[0-9]+/index.html");
		
		ScheduleThread scheduleThread = new ScheduleThread(spider);
		scheduleThread.start();
		
		try {
			while (scheduleThread.getState() != State.TERMINATED) {
				scheduleThread.join(60 * 1000);
				String strLog = spider.getSpiderCount().toString();
				logger.info(strLog);
				System.out.println(strLog);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		logger.info("CaribbeanPageSchedule:END");
	}
}
