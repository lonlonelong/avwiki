package com.killxdcj.avwiki.schedule;

import com.killxdcj.avwiki.spider.Spider;

public class ScheduleThread extends Thread {
	private Spider spider;
	
	public ScheduleThread(Spider spider) {
		this.spider = spider;
	}
	
	@Override  
    public void run()  
    {  
		spider.Start();
    } 
}
