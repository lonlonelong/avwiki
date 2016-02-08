package com.killxdcj.avwiki.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.killxdcj.avwiki.spider.Spider;
import com.killxdcj.avwiki.spider.SpiderCount;

@Component
public class MonitorContext {
	private List<SpiderCount> spiderCountEnd = new ArrayList<SpiderCount>();
	private Map<String, Spider> spiderCountMap = new HashMap<String, Spider>();
	
	public void registSpider(Spider spider) {
		spiderCountMap.put(spider.getSign(), spider);
	}
	
	public void unRegistSpider(Spider spider) {
		spiderCountMap.remove(spider.getSign());
		spiderCountEnd.add(spider.getSpiderCount());
	}
	
	public String getCurrentSpiderCount() {
		String strCount = "--------------------------------------------\r\n";
		strCount += "目前正在运行的爬虫\r\n";
		strCount += "--------------------------------------------\r\n";
		for (String sign : spiderCountMap.keySet()) {
			strCount += spiderCountMap.get(sign).getSpiderCount().toString() + "\r\n\r\n";
		}
		
		strCount += "--------------------------------------------\r\n";
		strCount += "已经爬取完成的爬虫\r\n";
		strCount += "--------------------------------------------\r\n";
		for (SpiderCount spiderCount : spiderCountEnd) {
			strCount += spiderCount.toString() + "\r\n\r\n";
		}
		
		return strCount;
	}
	
	public String getDailySpiderCount() {
		String strCount = getCurrentSpiderCount();
		spiderCountEnd.clear();
		return strCount;
	}
}
