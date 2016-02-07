package com.killxdcj.avwiki.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class AvwikiContextUtil implements ApplicationContextAware {

	private static ApplicationContext applicationContext;
	
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		AvwikiContextUtil.applicationContext = applicationContext;
	}
	
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	public static <T> T getBean(String name) {
		return (T)applicationContext.getBean(name);
	}
	
	public static <T> T getBean(Class<T> type) {
		return (T)applicationContext.getBeansOfType(type);
	}

}
