package com.killxdcj.avwiki.schedule;

import java.lang.Thread.State;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.killxdcj.avwiki.context.MonitorContext;
import com.killxdcj.avwiki.mail.AvwikiMailUtil;
import com.killxdcj.avwiki.spider.Spider;
import com.killxdcj.avwiki.spider.SpiderCount;
import com.killxdcj.avwiki.spider.TokyoHotPageProcessor;

@Component
public class TokyoHotSchedule {
	private static final Logger logger = Logger.getLogger(TokyoHotSchedule.class);
	
	@Autowired
	private MonitorContext monitorContext;
	
	@Scheduled(cron="0 30 0 * * ?")//每天0点30触发
	public void doSchedule() {
		logger.info("TokyoHotSchedule:START");
		
		Spider spider = new Spider("Tokyo-Hot");
		spider.setPageProcessor(new TokyoHotPageProcessor());
		spider.addSeed("http://my.tokyo-hot.com/product/?page=1");
		spider.addIncludeRegex("http://my.tokyo-hot.com/product/\\?page=[0-9]+");
		spider.addIncludeRegex("http://my.tokyo-hot.com/product/[0-9]+/");
		spider.Init();
		
		AvwikiMailUtil.sendNotifyMailAsync("定时任务", "Tokyo-Hot 抓取任务开始\r\n" + spider.getSpiderCount().toString());

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
			logger.error("TokyoHotSchedule:ERR " + e.getMessage());
		}
		monitorContext.unRegistSpider(spider);
		AvwikiMailUtil.sendNotifyMailAsync("定时任务", "Tokyo-Hot 抓取任务结束\r\n" + spider.getSpiderCount().toString());
		
		logger.info("TokyoHotSchedule:END");
	}
}
