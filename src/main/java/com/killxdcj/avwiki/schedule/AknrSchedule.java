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
import com.killxdcj.avwiki.spider.AknrPageProcessor;

@Component
public class AknrSchedule {
private static final Logger logger = Logger.getLogger(AknrSchedule.class);
	
	@Autowired
	private MonitorContext monitorContext;
	
	@Scheduled(cron="0 0 1 * * ?")//ÿ��1�㴥��
	public void doSchedule() {
		logger.info("AknrSchedule:START");
		
		Spider spider = new Spider("Aknr");
		spider.setPageProcessor(new AknrPageProcessor());
		spider.addSeed("http://www.aknr.com/home/");
		spider.addIncludeRegex("http://www.aknr.com/works/fset-[0-9]+/");
		spider.addIncludeRegex("http://www.aknr.com/[0-9]+/\\?post_type=works ");
		spider.Init();
		
		AvwikiMailUtil.sendNotifyMailAsync("��ʱ����", "Aknr ץȡ����ʼ\r\n" + spider.getSpiderCount().toString());

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
					AvwikiMailUtil.sendNotifyMailAsync("AVWIKI �����쳣",
							spiderCountOld.toString() + "\r\n" + spiderCountNew.toString());
				}
				spiderCountOld = spiderCountNew;
				logger.info(spiderCountOld.getNormalStr());
			}
		} catch (InterruptedException e) {
			logger.error("AknrSchedule:ERR " + e.getMessage());
		}
		monitorContext.unRegistSpider(spider);
		AvwikiMailUtil.sendNotifyMailAsync("��ʱ����", "Aknr ץȡ�������\r\n" + spider.getSpiderCount().toString());
		
		logger.info("AknrSchedule:END");
	}
}
