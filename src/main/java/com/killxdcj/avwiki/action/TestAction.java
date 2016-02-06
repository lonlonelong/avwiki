package com.killxdcj.avwiki.action;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("test")
public class TestAction {
	private static final Logger logger = Logger.getLogger(TestAction.class);

	@RequestMapping("test.do")
	public ModelAndView test() {
		ModelAndView view = new ModelAndView();
		view.addObject("result", "SUCCESS");
		view.setViewName("result");
		logger.info("Test !");
		return view;
	}
}
