package com.killxdcj.avwiki.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.killxdcj.avwiki.context.MonitorContext;
import com.killxdcj.avwiki.mail.AvwikiMailUtil;

@Controller
public class MonitorAction {

	@Autowired
	MonitorContext monitorContext;
	
	@RequestMapping("monitor.do")
	public ModelAndView all() {
		ModelAndView view = new ModelAndView();
		view.addObject("result", monitorContext.getCurrentSpiderCount());
		view.setViewName("result");
		
		AvwikiMailUtil.sendNotifyMailAsync("AVWIKI 运行日报", monitorContext.getCurrentSpiderCount());
		return view;
	}
}
