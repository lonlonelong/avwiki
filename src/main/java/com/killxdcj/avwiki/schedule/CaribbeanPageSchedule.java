package com.killxdcj.avwiki.schedule;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.killxdcj.avwiki.caribbean.CaribbeanPageProcessor;
import com.killxdcj.avwiki.spider.Spider;

@Component
public class CaribbeanPageSchedule {
	private static final Logger logger = Logger.getLogger(CaribbeanPageSchedule.class);
	
	@Scheduled(cron="0 0 0 * * ?")//ÿ��0�㴥��
	public void doSchedule() {
		logger.info("CaribbeanPageSchedule:START");
		
		Spider spider = new Spider("caribbeancom");
		spider.setPageProcessor(new CaribbeanPageProcessor());
		spider.addSeed("http://www.caribbeancompr.com/listpages/1.html");
		spider.addIncludeRegex("http://www.caribbeancompr.com/listpages/[0-9]+.html");
		spider.addIncludeRegex("http://www.caribbeancompr.com/moviepages/[0-9]+_[0-9]+/index.html");
		spider.Start();
		
		logger.info("CaribbeanPageSchedule:END");
	}
}