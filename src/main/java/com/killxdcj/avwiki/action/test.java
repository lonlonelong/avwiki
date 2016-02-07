package com.killxdcj.avwiki.action;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		 testRegex();
		getUrlContent("http://www.caribbeancompr.com/listpages/1.html");
	}

	public static void testRegex() {
		String regex1 = "http://www.caribbeancompr.com/moviepages/[0-9]+_[0-9]+/index.html";
		String regex2 = "http://www.caribbeancompr.com/listpages/[0-9]+.html";
		
		if (Pattern.matches(regex1, "http://www.caribbeancompr.com/listpages/1.html")) {
			System.out.println("match");
		} else {
			System.out.println("no match");
		}
		
		if (Pattern.matches(regex2, "http://www.caribbeancompr.com/listpages/1.html")) {
			System.out.println("match");
		} else {
			System.out.println("no match");
		}
	}
	
	private static String getUrlContent(String url) {
		String strHtml = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse httpResponse = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			httpResponse = httpClient.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				byte[] bytes = EntityUtils.toByteArray(httpEntity);
				String strTemp = new String(bytes, "UTF-8");
				String regex = "(<[^>]*(C|c)(O|o)(N|n)(T|t)(E|e)(N|n)(T|t)-(T|t)(Y|y)(P|p)(E|e)[^>]*>)";
				Pattern pattern = Pattern.compile(regex);  
		        Matcher matcher = pattern.matcher(strTemp);
		        if (matcher.find()) {
		        	strTemp = matcher.group();
//		        	System.out.println(strHtml);
				}
		        regex = "((C|c)(H|h)(A|a)(R|r)(S|s)(E|e)(T|t)=[^;\"]+(;|\"))";
		        pattern = Pattern.compile(regex);  
		        matcher = pattern.matcher(strTemp);
		        if (matcher.find()) {
		        	strTemp = matcher.group();
//		        	System.out.println(strHtml);
				}
		        
		        int nStart = strTemp.indexOf('=') + 1;
		        int nEnd = strTemp.indexOf(';');
		        if (nEnd == -1) {
		        	nEnd = strTemp.indexOf('\"');
				}
//		        System.out.println(strHtml.substring(nStart, nEnd));
		        strHtml = new String(bytes, strTemp.substring(nStart, nEnd));
		        System.out.println(strHtml);
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
		//System.out.println(strHtml);
		return strHtml;
	}
}
