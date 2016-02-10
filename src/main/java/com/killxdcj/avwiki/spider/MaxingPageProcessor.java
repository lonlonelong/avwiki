package com.killxdcj.avwiki.spider;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.killxdcj.avwiki.context.AvwikiContextUtil;
import com.killxdcj.avwiki.service.MovieInfoService;
import com.killxdcj.avwiki.util.HttpUtil;

public class MaxingPageProcessor implements PageProcessor {

	public void processPage(String url, String html) {
		String regex = "http://www.maxing.jp/shop/pid/[0-9]+.html";
		if (!Pattern.matches(regex, url)) return;
		
		MovieInfoService movieInfoService = AvwikiContextUtil.getBean("movieInfoService");
		Map<Object, Object> movieInfoMap = new HashMap<Object, Object>();
		
		movieInfoMap.put("company", "Maxing");
		
		Document document = Jsoup.parse(html);
		movieInfoMap.put("title", document.getElementsByTag("h1").first().text());
		
		Elements details = document.getElementsByClass("pDetailDl").first().getElementsByTag("dd");
		movieInfoMap.put("number", details.get(0).text());
		movieInfoMap.put("studio", details.get(1).text());
		movieInfoMap.put("playtime", details.get(2).text().trim());
		movieInfoMap.put("issuedate", details.get(3).text());
		
//		メーカー    Maker  
		
//		レーベル		Label
		if (details.get(5).getElementsByTag("a").size() > 0) {
			movieInfoMap.put("label", details.get(5).getElementsByTag("a").first().text());
		}
//		シリーズ		series
		if (details.get(6).getElementsByTag("a").size() > 0) {
			movieInfoMap.put("series", details.get(6).getElementsByTag("a").first().text());
		}
		movieInfoMap.put("player", details.get(7).text().replace('、', '#').trim());
//		ジャンル           类型
		Elements cats = details.get(8).getElementsByTag("a");
		String strCat = "";
		for (int i = 0; i < cats.size(); i++) {
			if (i == 0) {
				strCat += cats.get(i).text();
			} else {
				strCat += "#" + cats.get(i).text();
			}
		}
		movieInfoMap.put("category", strCat.trim());
		
		movieInfoMap.put("comment", details.get(9).text().trim());
		
		movieInfoService.insertMovieInfo(movieInfoMap);
		
		// 图片无法即时抓取到 有反爬措施
		// 小图url http://www.maxing.jp/product_img/番号/P0002.jpg
		// 大图url http://www.maxing.jp/product_img/番号/P0002.jpg
		
		String largeImgUrl = document.getElementsByClass("pImg").first().getElementsByTag("a").first().attr("href");
		String smallImgUrl = document.getElementsByClass("pImg").first().getElementsByTag("img").first().attr("src");
		String smallImgLocalPath = String.format("/avwiki/images/%s_s.jpg", movieInfoMap.get("number"));
		String largeImgLocalPath = String.format("/avwiki/images/%s_l.jpg", movieInfoMap.get("number"));
		HttpUtil.saveHttpImg(smallImgUrl, smallImgLocalPath);
		HttpUtil.saveHttpImg(largeImgUrl, largeImgLocalPath);
	}
	
	

}
