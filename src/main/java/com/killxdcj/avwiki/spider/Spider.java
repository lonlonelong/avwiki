package com.killxdcj.avwiki.spider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.midi.MidiDevice.Info;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import com.killxdcj.avwiki.context.AvwikiContextUtil;
import com.killxdcj.avwiki.entiy.SpiderRecord;
import com.killxdcj.avwiki.service.SpiderRecordServiceImpl;

public class Spider {
	private static final Logger logger = Logger.getLogger(Spider.class);

	private SpiderRecordServiceImpl spiderRecordService = null;
	private PageProcessor pageProcessor = null;
	
	private List<String> seedList = new ArrayList<String>();
	private Map<String, String> urlUnVisit = new HashMap<String, String>();
	private Map<String, String> urlVisited = new HashMap<String, String>();
	private List<String> includeRegexList = new ArrayList<String>();
	private List<String> excludeRegexList = new ArrayList<String>();
	private String sign;
	private SpiderCount spiderCount;
	
	public void Init() {
		spiderCount = new SpiderCount(sign);
		spiderRecordService = AvwikiContextUtil.getBean("spiderRecordService");
		loadSpiderInfo();
	}
	
	public void Start() {
		while (!urlUnVisit.keySet().isEmpty()) {
			String[] urlArray = urlUnVisit.keySet().toArray(new String[0]);
			for (String url : urlArray) {
				try {
					String strHtml = getUrlContent(url.trim());
					if (null != strHtml) {
						captureUrl(url, strHtml);
						if (null != pageProcessor) {
							pageProcessor.processPage(url, strHtml);
						}
						spiderRecordService.setRecordVisited(urlUnVisit.get(url));
						spiderCount.increaseVisitOK();
					} else {
						logger.error("Spider visit url error : " + url);
						spiderCount.increaseVisitErr();
					}
				} catch (Exception e) {
					spiderCount.increaseVisitErr();
					logger.error(String.format("Spider Error sign:%s url:%s msg:%s", sign, url, e.getMessage()));
				} finally {
					urlVisited.put(url, urlUnVisit.get(url));
					urlUnVisit.remove(url);
				}
			}
		}
		
		spiderCount.setEndDate(new Date());
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
			} else if (urlVisited.containsKey(seed)) {
				urlUnVisit.put(seed, urlVisited.get(seed));
				urlVisited.remove(seed);
			}
		}
	}
	
	private String getUrlContent(String url) {
		String strHtml = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse httpResponse = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.79 Safari/537.1");
			httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
	
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
		
		String urlBase = null;
		nIndex = url.indexOf("?");
		if (nIndex != -1) {
			urlBase = url.substring(0, nIndex);
		}
		
		//String patternString = "<[a|A]\\s+href=([^>]*\\s*>)";
		String patternString = "href=\"[^\"]*\"";
        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(strHtml);
        while (matcher.find()) {
            String tempURL = matcher.group();
            tempURL = tempURL.substring(tempURL.indexOf("\"") + 1);
            if (!tempURL.contains("\""))
                continue;
            tempURL = tempURL.substring(0, tempURL.indexOf("\""));
            if (!tempURL.startsWith("http")) {
            	if (tempURL.startsWith("/")) {
            		tempURL = urlHost + tempURL;
				} else if (tempURL.startsWith("?") && urlBase != null) {
					tempURL = urlBase + tempURL;
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
			}
        }
        
		patternString = "href='[^']*'";
        pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(strHtml);
        while (matcher.find()) {
            String tempURL = matcher.group();
            tempURL = tempURL.substring(tempURL.indexOf("'") + 1);
            if (!tempURL.contains("'"))
                continue;
            tempURL = tempURL.substring(0, tempURL.indexOf("'"));
            if (!tempURL.startsWith("http")) {
            	if (tempURL.startsWith("/")) {
            		tempURL = urlHost + tempURL;
				} else if (tempURL.startsWith("?") && urlBase != null) {
					tempURL = urlBase + tempURL;
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
	
	public Spider(String signString) {
		sign = signString;
	}
	
	private String getHtmlCharset(String strHtml) {
		String strCharset = "utf-8";
		String regex = "(<(M|m)(E|e)(T|t)(A|a)[^>]*(C|c)(H|h)(A|a)(R|r)(S|s)(E|e)(T|t)[^>]*>)";
		Pattern pattern = Pattern.compile(regex);  
        Matcher matcher = pattern.matcher(strHtml);
        if (matcher.find()) {
        	strHtml = matcher.group();
        	regex = "((C|c)(H|h)(A|a)(R|r)(S|s)(E|e)(T|t)=\"{0,1}[^;\"]+(;|\"))";
            pattern = Pattern.compile(regex);  
            matcher = pattern.matcher(strHtml);
            if (matcher.find()) {
            	strHtml = matcher.group();
            	int nStart = strHtml.indexOf("=\"");
		        if (nStart == -1) {
		        	nStart = strHtml.indexOf('=') + 1;
				} else {
					nStart = nStart + 2;
				}
		        
		        int nEnd = strHtml.indexOf(';');
		        if (nEnd == -1) {
		        	nEnd = strHtml.indexOf('\"', nStart);
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

	public PageProcessor getPageProcessor() {
		return pageProcessor;
	}

	public void setPageProcessor(PageProcessor pageProcessor) {
		this.pageProcessor = pageProcessor;
	}

	public SpiderCount getSpiderCount() {
		spiderCount.setVisitedCount(urlVisited.size());
		spiderCount.setUnVisitCount(urlUnVisit.size());
		return spiderCount;
	}

	public String getSign() {
		return sign;
	}
	
	
}
