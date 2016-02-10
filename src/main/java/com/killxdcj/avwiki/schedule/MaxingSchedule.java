package com.killxdcj.avwiki.schedule;

import java.lang.Thread.State;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.killxdcj.avwiki.context.MonitorContext;
import com.killxdcj.avwiki.mail.AvwikiMailUtil;
import com.killxdcj.avwiki.spider.MaxingPageProcessor;
import com.killxdcj.avwiki.spider.Spider;
import com.killxdcj.avwiki.spider.SpiderCount;

@Component
public class MaxingSchedule {
	private static final Logger logger = Logger.getLogger(MaxingSchedule.class);
	
	@Autowired
	private MonitorContext monitorContext;
	
	@Scheduled(cron="0 30 1 * * ?")//每天1点30触发
	public void doSchedule() {
		logger.info("MaxingSchedule:START");
		
		Spider spider = new Spider("Maxing");
		spider.setPageProcessor(new MaxingPageProcessor());
		spider.addSeed("http://www.maxing.jp/shop/src/page/1.html");
		spider.addIncludeRegex("http://www.maxing.jp/shop/src/page/[0-9]+.html");
		spider.addIncludeRegex("http://www.maxing.jp/shop/pid/[0-9]+.html");
		spider.Init();
		
		AvwikiMailUtil.sendNotifyMailAsync("定时任务", "Maxing 抓取任务开始\r\n" + spider.getSpiderCount().toString());

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
				logger.info(spiderCountOld.getNormalStr());
			}
		} catch (InterruptedException e) {
			logger.error("MaxingSchedule:ERR " + e.getMessage());
		}
		monitorContext.unRegistSpider(spider);
		AvwikiMailUtil.sendNotifyMailAsync("定时任务", "Maxing 抓取任务结束\r\n" + spider.getSpiderCount().toString());
		
		logger.info("MaxingSchedule:END");
	}
}
