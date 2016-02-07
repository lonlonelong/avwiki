package com.killxdcj.avwiki.spider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.killxdcj.avwiki.entiy.SpiderRecord;
import com.killxdcj.avwiki.service.SpiderRecordServiceImpl;

@Component
public class Spider {
	private static final Logger logger = Logger.getLogger(Spider.class);

	@Autowired
	private SpiderRecordServiceImpl spiderRecordService;
	
	private List<String> seedList = new ArrayList<String>();
	private Map<String, String> urlUnVisit = new HashMap<String, String>();
	private Map<String, String> urlVisited = new HashMap<String, String>();
	private List<String> includeRegexList = new ArrayList<String>();
	private List<String> excludeRegexList = new ArrayList<String>();
	private String sign = "caribbean";
	
	public void Start() {
		loadSpiderInfo();
		doSpider();
	}
	
	private void loadSpiderInfo() {
		List<SpiderRecord> spiderRecordUnVisit = spiderRecordService.getSpiderRecord(sign, false);
		for (SpiderRecord spiderRecord : spiderRecordUnVisit) {
			urlUnVisit.put(spiderRecord.getUrl(), spiderRecord.getId());
		}

		List<SpiderRecord> spiderRecordVisited = spiderRecordService.getSpiderRecord(sign, true);
		for (SpiderRecord spiderRecord : spiderRecordVisited) {
			urlVisited.put(spiderRecord.getUrl(), spiderRecord.getId());
		}
		
		for (String seed : seedList) {
			if (!urlVisited.containsKey(seed) && !urlUnVisit.containsKey(seed)) {
				Map<Object, Object> paramMap = new HashMap<Object, Object>();
				paramMap.put("sign", sign);
				paramMap.put("url", seed);
				String id = spiderRecordService.insertSpiderRecord(paramMap);
				urlUnVisit.put(seed, id);
			}
		}
	}
	
	private void doSpider() {
		while (!urlUnVisit.keySet().isEmpty()) {
			String[] urlArray = urlUnVisit.keySet().toArray(new String[0]);
			for (String url : urlArray) {
				String strHtml = getUrlContent(url);
				if (null != strHtml) {
					captureUrl(url, strHtml);
					/*
					 * 做内容分析 
					 * */
				} else {
					
				}
				updateRecord(url);
				System.out.println("OK " + url);
				// 提取url 添加 url
				// 调用用户 匹配
				// 更新抓取记录
			}
		}
	}
	
	private String getUrlContent(String url) {
		String strHtml = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse httpResponse = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			httpResponse = httpClient.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				byte[] bytes = EntityUtils.toByteArray(httpEntity);
				strHtml = new String(bytes, getHtmlCharset(new String(bytes, "UTF-8")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			try {
				if (null != httpResponse) {
					httpResponse.close();
				}
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return strHtml;
	}
	
	private void captureUrl(String url, String strHtml) {
		String urlHost = null;
		int nIndex = url.indexOf("://");
		if (nIndex != -1) {
			if ((nIndex = url.indexOf('/', nIndex + "://".length())) != -1) {
				urlHost = url.substring(0, nIndex);
			} else {
				urlHost = url;
			}
		}
		
		String patternString = "<[a|A]\\s+href=([^>]*\\s*>)";
        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(strHtml);
        // 初次匹配到的url是形如：
        // <a href="http://bbs.life.xxx.com.cn/" target="_blank">
        // 为此，需要进行下一步的处理，把真正的url抽取出来，
        // 可以对于前两个"之间的部分进行记录得到url
        while (matcher.find()) {
            String tempURL = matcher.group();
            tempURL = tempURL.substring(tempURL.indexOf("\"") + 1);
            if (!tempURL.contains("\""))
                continue;
            tempURL = tempURL.substring(0, tempURL.indexOf("\""));
            if (!tempURL.startsWith("http")) {
            	if (tempURL.startsWith("/")) {
            		tempURL = urlHost + tempURL;
				} else {
					tempURL = urlHost + "/" + tempURL;
				}
            }

            if (!isExcludeUrl(tempURL) && isIncludeUrl(tempURL) && !urlUnVisit.containsKey(tempURL) && !urlVisited.containsKey(tempURL)) {
				Map<Object, Object> paramMap = new HashMap<Object, Object>();
				paramMap.put("url", tempURL);
				paramMap.put("sign", sign);
				String id = spiderRecordService.insertSpiderRecord(paramMap);
				urlUnVisit.put(tempURL, id);
				
				System.out.println("ADD " + tempURL);
			}
        }

	}
	
	private boolean isExcludeUrl(String url) {
		for (String regex : excludeRegexList) {
			if (Pattern.matches(regex, url)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isIncludeUrl(String url) {
		for (String regex : includeRegexList) {
			if (Pattern.matches(regex, url)) {
				return true;
			}
		}
		return false;
	}
	
	private void updateRecord(String url) {
		spiderRecordService.setRecordVisited(urlUnVisit.get(url));
		urlVisited.put(url, urlUnVisit.get(url));
		urlUnVisit.remove(url);
	}
	
//	public Spider(String signString) {
//		sign = signString;
//	}
	
	private String getHtmlCharset(String strHtml) {
		String strCharset = "utf-8";
		String regex = "(<[^>]*(C|c)(O|o)(N|n)(T|t)(E|e)(N|n)(T|t)-(T|t)(Y|y)(P|p)(E|e)[^>]*>)";
		Pattern pattern = Pattern.compile(regex);  
        Matcher matcher = pattern.matcher(strHtml);
        if (matcher.find()) {
        	strHtml = matcher.group();
        	regex = "((C|c)(H|h)(A|a)(R|r)(S|s)(E|e)(T|t)=[^;\"]+(;|\"))";
            pattern = Pattern.compile(regex);  
            matcher = pattern.matcher(strHtml);
            if (matcher.find()) {
            	strHtml = matcher.group();
            	int nStart = strHtml.indexOf('=') + 1;
                int nEnd = strHtml.indexOf(';');
                if (nEnd == -1) {
                	nEnd = strHtml.indexOf('\"');
        		}
                strCharset = strHtml.substring(nStart, nEnd);
    		}
		}
        
        return strCharset;
	}
	
	public void addSeed(String seedString) {
		seedList.add(seedString);
	}
	
	public void addIncludeRegex(String regexString) {
		includeRegexList.add(regexString);
	}
	
	public void addExcludeRegex(String regexString) {
		excludeRegexList.add(regexString);
	}
	
	
}
