package com.killxdcj.avwiki.spider;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.killxdcj.avwiki.context.AvwikiContextUtil;
import com.killxdcj.avwiki.service.MovieInfoService;
import com.killxdcj.avwiki.util.HttpUtil;

public class TokyoHotPageProcessor implements PageProcessor {

	public void processPage(String url, String html) {
		String regex = "http://my.tokyo-hot.com/product/[0-9]+/";
		if (!Pattern.matches(regex, url)) return;
		
		MovieInfoService movieInfoService = AvwikiContextUtil.getBean("movieInfoService");
		Map<Object, Object> movieInfoMap = new HashMap<Object, Object>();
		
		Document document = Jsoup.parse(html);
		movieInfoMap.put("company", "Tokyo-Hot");
		movieInfoMap.put("title", document.getElementsByClass("contents").first().getElementsByTag("h2").first().text());
		movieInfoMap.put("comment", document.getElementsByClass("sentence").first().text());

		String strNumber = "";
		
		Elements infos = document.getElementsByClass("info");
		for (int i = 0; i < infos.size(); i++) {
			Elements dts = infos.get(i).getElementsByTag("dt");
			Elements dds = infos.get(i).getElementsByTag("dd");
			for (int j = 0; j < dts.size(); j++) {
				String strdt = dts.get(j).text();
				if (strdt.equals("������")) {
					String strPlayer = "";
					Elements players = dds.get(j).getElementsByTag("a");
					for (int k = 0; k < players.size(); k++) {
						if(k == 0) {
							strPlayer = strPlayer + players.get(k).text();
						} else {
							strPlayer = strPlayer + "#" + players.get(k).text();
						}
					}
					movieInfoMap.put("player", strPlayer);
				} else if (strdt.equals("����`��")) {
					String strSeries = "";
					Elements series = dds.get(j).getElementsByTag("a");
					for (int k = 0; k < series.size(); k++) {
						if (k == 0) {
							strSeries += series.get(k).text();
						} else {
							strSeries = strSeries + "#" + series.get(k).text();
						}
					}
					movieInfoMap.put("series", strSeries);
				} else if (strdt.equals("���ƥ���")) {
					String strCat = "";
					Elements cats = dds.get(j).getElementsByTag("a");
					for (int k = 1; k < cats.size(); k++) {
						if (k == 0) {
							strCat += cats.get(k).text();
						} else {
							strCat = strCat + "#" + cats.get(k).text();
						}
					}
					movieInfoMap.put("category", strCat);
				} else if (strdt.equals("�����_ʼ��")) {
					movieInfoMap.put("issuedate", dds.get(j).text());
				} else if (strdt.equals("���h�r�g")) {
					movieInfoMap.put("playtime", dds.get(j).text());
				} else if (strdt.equals("��Ʒ����")) {
					strNumber = dds.get(j).text();
					movieInfoMap.put("number", strNumber);
				}
			}
		}
		
		movieInfoService.insertMovieInfo(movieInfoMap);

		Elements elements = document.getElementsByClass("flowplayer");
		String largeImgUrl = document.getElementsByClass("flowplayer").first().getElementsByTag("video ").first().attributes().get("poster");
		String smallImgUrl = largeImgUrl.replaceFirst("820x462", "220x124");
		String smallImgLocalPath = String.format("/avwiki/images/%s_s.jpg", strNumber);
		String largeImgLocalPath = String.format("/avwiki/images/%s_l.jpg", strNumber);
		HttpUtil.saveHttpImg(smallImgUrl, smallImgLocalPath);
		HttpUtil.saveHttpImg(largeImgUrl, largeImgLocalPath);
	}

}
