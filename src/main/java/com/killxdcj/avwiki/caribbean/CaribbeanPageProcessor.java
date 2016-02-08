package com.killxdcj.avwiki.caribbean;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.killxdcj.avwiki.context.AvwikiContextUtil;
import com.killxdcj.avwiki.service.MovieInfoService;
import com.killxdcj.avwiki.spider.PageProcessor;
import com.killxdcj.avwiki.util.HttpUtil;

public class CaribbeanPageProcessor implements PageProcessor {
	
	private MovieInfoService movieInfoService;

	public void processPage(String url, String html) {
		String regex = "http://www.caribbeancompr.com/moviepages/[0-9]+_[0-9]+/index.html";
		if (!Pattern.matches(regex, url)) return;

		movieInfoService = AvwikiContextUtil.getBean("movieInfoService");
		Map<Object, Object> movieInfoMap = new HashMap<Object, Object>();
		
		int nIndex = "http://www.caribbeancompr.com/moviepages/".length();
		String movieId = url.substring(nIndex, url.indexOf("/", nIndex));
		movieInfoMap.put("number", movieId);
		movieInfoMap.put("company", "caribbeancom");
		
		String smallImgUrl = String.format("http://www.caribbeancompr.com/moviepages/%s/images/main_s.jpg", movieId);
		String largeImgUrl = String.format("http://www.caribbeancompr.com/moviepages/%s/images/l_l.jpg", movieId);
		String smallImgLocalPath = String.format("/avwiki/images/%s_s.jpg", movieId);
		String largeImgLocalPath = String.format("/avwiki/images/%s_l.jpg", movieId);
		HttpUtil.saveHttpImg(smallImgUrl, smallImgLocalPath);
		HttpUtil.saveHttpImg(largeImgUrl, largeImgLocalPath);
		
		
		Document document = Jsoup.parse(html);
		Elements elements = document.getElementsByClass("main-content-movieinfo");
		if (!elements.isEmpty()) {
			Element content = elements.first();
			movieInfoMap.put("title", content.getElementsByClass("video-detail").first().getElementsByTag("h1").first().text());
			movieInfoMap.put("comment", content.getElementsByClass("movie-comment").first().getElementsByTag("p").first().text());
			
			Element movieinfo = content.getElementsByClass("movie-info").first();
			Elements movieinfos = movieinfo.getElementsByTag("dl");
			for (int i = 0; i < movieinfos.size(); i++) {
				Element element = movieinfos.get(i);
				String name = element.getElementsByTag("dt").first().text();
				if (name.equals("出演:")) {
					Elements players = element.getElementsByTag("dd").first().getElementsByTag("strong");
					String strPalyer = "";
					if (players.size() > 0) {
						strPalyer += players.get(0).getElementsByTag("a").first().text();
					}
					for (int j = 1; j < players.size(); j++) {
						strPalyer += "#" + players.get(j).getElementsByTag("a").first().text();
					}
					movieInfoMap.put("player", strPalyer);
				} else if (name.equals("カテゴリー:")) {//Category
					Elements cats = element.getElementsByTag("dd");
					String strCat = "";
					if (cats.size() > 0) {
						strCat += cats.get(0).getElementsByTag("a").first().text();
					}
					for (int j = 1; j < cats.size(); j++) {
						strCat += "#" + cats.get(j).getElementsByTag("a").first().text();
					}
					movieInfoMap.put("category", strCat);
				} else if (name.equals("販売日:")) {
					movieInfoMap.put("issuedate", element.getElementsByTag("dd").first().text());
				} else if (name.equals("再生時間:")) {
					movieInfoMap.put("playtime", element.getElementsByTag("dd").first().text());
				} else if (name.equals("スタジオ:")) {//studio
					movieInfoMap.put("studio", element.getElementsByTag("dd").first().getElementsByTag("a").first().text());
				} else if (name.equals("シリーズ:")) {//series
					movieInfoMap.put("series", element.getElementsByTag("dd").first().getElementsByTag("a").first().text());
				} else if (name.equals("レーベル:")) {
					movieInfoMap.put("label", element.getElementsByTag("dd").first().getElementsByTag("a").first().text());
				}
			}
			
			movieInfoService.insertMovieInfo(movieInfoMap);
		}
	}
}
