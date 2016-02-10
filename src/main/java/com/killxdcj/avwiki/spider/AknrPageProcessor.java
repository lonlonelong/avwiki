package com.killxdcj.avwiki.spider;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.killxdcj.avwiki.context.AvwikiContextUtil;
import com.killxdcj.avwiki.service.MovieInfoService;
import com.killxdcj.avwiki.util.HttpUtil;

public class AknrPageProcessor implements PageProcessor {

	public void processPage(String url, String html) {
		String regex = "http://www.aknr.com/works/fset-[0-9]+/";
		if (!Pattern.matches(regex, url)) return;

		MovieInfoService movieInfoService = AvwikiContextUtil.getBean("movieInfoService");
		Map<Object, Object> movieInfoMap = new HashMap<Object, Object>();
		
		movieInfoMap.put("company", "Aknr");
		
		Document document = Jsoup.parse(html);
		
		movieInfoMap.put("title", document.getElementsByTag("h1").first().text());
		
		Element info = document.getElementById("info");
		movieInfoMap.put("issuedate", info.getElementsByTag("p").get(1).text());
		Elements players = info.getElementsByTag("span");
		String strPlayer = "";
		for (int j = 0; j < players.size(); j++) {
			if (j == 0) {
				strPlayer += players.get(j).getElementsByTag("a").first().text();
			} else {
				strPlayer += "#" + players.get(j).getElementsByTag("a").first().text();
			}
		}
		movieInfoMap.put("player", strPlayer);
		
		Elements elements = document.getElementsByClass("data_item2");
		movieInfoMap.put("studio", elements.get(0).text());
		movieInfoMap.put("number", elements.get(1).text());
		movieInfoMap.put("playtime", elements.get(2).text());
		
		movieInfoService.insertMovieInfo(movieInfoMap);
		
		String strNumber = (String) movieInfoMap.get("number");
		Element img = document.getElementById("jktimg_l2");
		String largeImgUrl = document.getElementById("jktimg_l2").getElementsByTag("a").first().attr("href");
		String smallImgUrl = largeImgUrl.replace("_l", "_m");
		String smallImgLocalPath = String.format("/avwiki/images/%s_s.jpg", strNumber);
		String largeImgLocalPath = String.format("/avwiki/images/%s_l.jpg", strNumber);
		HttpUtil.saveHttpImg(smallImgUrl, smallImgLocalPath);
		HttpUtil.saveHttpImg(largeImgUrl, largeImgLocalPath);
	}

}
