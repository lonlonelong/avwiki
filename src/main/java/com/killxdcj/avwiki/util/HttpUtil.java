package com.killxdcj.avwiki.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpUtil {
	public static boolean saveHttpImg(String imgUrl, String localPath) {
		boolean bRet = false;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse httpResponse = null;
		try {
			HttpGet httpGet = new HttpGet(imgUrl);
			httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.79 Safari/537.1");
			httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			httpResponse = httpClient.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				InputStream inputStream = httpResponse.getEntity().getContent();
				File file = new File(localPath);  
	            if (file == null || !file.exists()) {  
	                file.createNewFile();  
	            }  

	            FileOutputStream fos = new FileOutputStream(file);  
	            byte[] buf = new byte[1024];  
	            int len = 0;  
	            while ((len = inputStream.read(buf)) != -1) {  
	                fos.write(buf, 0, len);  
	            }  
	            fos.flush();  
	            fos.close();
	            bRet = true;
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
		return bRet;
	}
}
