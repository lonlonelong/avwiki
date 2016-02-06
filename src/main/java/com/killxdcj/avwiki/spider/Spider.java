package com.killxdcj.avwiki.spider;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Spider {
	private static final Logger logger = Logger.getLogger(Spider.class);

	private List<String> urlToVisitList = new ArrayList<String>();
	private List<String> urlVisitedList = new ArrayList<String>();
	private List<String> includeRegexList = new ArrayList<String>();
	private List<String> excludeRegexList = new ArrayList<String>();
	private String signString;
	
	public void Start() {
		
	}
	
	private void loadSpiderInfo() {
		
	}
	
	public Spider(String sign) {
		signString = sign;
	}
	
	public void addSeed(String seedString) {
		urlToVisitList.add(seedString);
	}
	
	public void addIncludeRegex(String regexString) {
		includeRegexList.add(regexString);
	}
	
	public void addExcludeRegex(String regexString) {
		excludeRegexList.add(regexString);
	}
	
	
}
