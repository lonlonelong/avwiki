package com.killxdcj.avwiki.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.killxdcj.avwiki.entiy.SpiderRecord;
import com.killxdcj.avwiki.mapper.SpiderRecordMapperImpl;
import com.killxdcj.avwiki.service.SpiderRecordServiceImpl;
import com.killxdcj.avwiki.spider.Spider;

@Controller
@RequestMapping("test")
public class TestAction {
	private static final Logger logger = Logger.getLogger(TestAction.class);

	@Autowired
	private SpiderRecordMapperImpl spiderRecordMapperImpl;
	
	@Autowired
	private SpiderRecordServiceImpl spiderRecordServiceImpl;
	
	@Autowired
	private Spider spider;
	
	@RequestMapping("test.do")
	public ModelAndView test() {
		ModelAndView view = new ModelAndView();
		view.addObject("result", "SUCCESS");
		view.setViewName("result");
		logger.info("Test !");
		return view;
	}
	
	@RequestMapping("spiderrecordmapper.do")
	public ModelAndView testSpiderRecordMapper() {
		
		Map<Object, Object> paramMap = new HashMap<Object, Object>();
		paramMap.put("sign", "caribbean");
		paramMap.put("url", "http://killxdcj.com");
		String nId = spiderRecordMapperImpl.insertSpiderRecord(paramMap);
		
		paramMap.clear();
		paramMap.put("id", nId);
		paramMap.put("sign", "caribbeangp");
		paramMap.put("url", "http://www.killxdcj.com");
		int nAffect = spiderRecordMapperImpl.updateSpiderRecord(paramMap);
		
		paramMap.clear();
		paramMap.put("id", nId);
		paramMap.put("isDelete", "y");
		paramMap.put("visited", "y");
		nAffect = spiderRecordMapperImpl.updateSpiderRecord(paramMap);
		
		paramMap.clear();
		paramMap.put("sign", "caribbeangp");
		List<SpiderRecord> spiderRecords = spiderRecordMapperImpl.selectSpiderRecord(paramMap);
		
		ModelAndView view = new ModelAndView();
		view.addObject("result", "SUCCESS");
		view.setViewName("result");
		return view;
	}
	
	@RequestMapping("spiderrecordservice.do")
	public ModelAndView testSpiderRecordService() {
		
		Map<Object, Object> paramMap = new HashMap<Object, Object>();
		paramMap.put("sign", "caribbean");
		paramMap.put("url", "http://killxdcj.com");
		String id = spiderRecordServiceImpl.insertSpiderRecord(paramMap);
		
		spiderRecordServiceImpl.insertSpiderRecord(paramMap);
		
		paramMap.clear();
		paramMap.put("id", id);
		spiderRecordServiceImpl.setRecordVisited(id);
		
		List<SpiderRecord> spiderRecords = spiderRecordServiceImpl.getSpiderRecord("caribbean", true);
		
		spiderRecords = spiderRecordServiceImpl.getSpiderRecord("caribbean", false);
		
		ModelAndView view = new ModelAndView();
		view.addObject("result", "SUCCESS");
		view.setViewName("result");
		return view;
	}
	
	@RequestMapping("spider.do")
	public ModelAndView testSpider() {
		
		spider.addSeed("http://www.caribbeancompr.com/listpages/1.html");
		spider.addIncludeRegex("http://www.caribbeancompr.com/listpages/[0-9]+.html");
		spider.addIncludeRegex("http://www.caribbeancompr.com/moviepages/[0-9]+_[0-9]+/index.html");
		spider.Start();
		
		ModelAndView view = new ModelAndView();
		view.addObject("result", "SUCCESS");
		view.setViewName("result");
		return view;
	}
}
