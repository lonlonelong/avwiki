package com.killxdcj.avwiki.schedule;

import java.lang.Thread.State;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.killxdcj.avwiki.caribbean.CaribbeanPageProcessor;
import com.killxdcj.avwiki.context.MonitorContext;
import com.killxdcj.avwiki.mail.AvwikiMailUtil;
import com.killxdcj.avwiki.spider.Spider;
import com.killxdcj.avwiki.spider.SpiderCount;

@Component
public class CaribbeanPageSchedule {
	private static final Logger logger = Logger.getLogger(CaribbeanPageSchedule.class);
	
	@Autowired
	private MonitorContext monitorContext;
	
	@Scheduled(cron="0 0 0 * * ?")//每天0点触发
	public void doSchedule() {
		logger.info("CaribbeanPageSchedule:START");
		
		Spider spider = new Spider("caribbeancom");
		spider.setPageProcessor(new CaribbeanPageProcessor());
		spider.addSeed("http://www.caribbeancompr.com/listpages/1.html");
		spider.addIncludeRegex("http://www.caribbeancompr.com/listpages/[0-9]+.html");
		spider.addIncludeRegex("http://www.caribbeancompr.com/moviepages/[0-9]+_[0-9]+/index.html");
		spider.Init();
		
		AvwikiMailUtil.sendNotifyMailAsync("定时任务 Caribbean", "抓取任务开始\r\n" + spider.getSpiderCount().toString());

		monitorContext.registSpider(spider);
		
		ScheduleThread scheduleThread = new ScheduleThread(spider);
		scheduleThread.start();
		
		try {
			SpiderCount spiderCountOld = spider.getSpiderCount();
			while (scheduleThread.getState() != State.TERMINATED) {
				scheduleThread.join(5 * 60 * 1000);
				SpiderCount spiderCountNew = spider.getSpiderCount();
				if (spiderCountNew.getVisitErrCount() - spiderCountOld.getVisitErrCount() > 
				spiderCountNew.getVisitOKCount() - spiderCountOld.getVisitOKCount()) {
					AvwikiMailUtil.sendNotifyMailAsync("AVWIKI 运行异常",
							spiderCountOld.toString() + "\r\n" + spiderCountNew.toString());
				}
				spiderCountOld = spiderCountNew;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		monitorContext.unRegistSpider(spider);
		AvwikiMailUtil.sendNotifyMailAsync("定时任务 Caribbean", "抓取任务结束\r\n" + spider.getSpiderCount().toString());
		logger.info("CaribbeanPageSchedule:END");
	}
}
