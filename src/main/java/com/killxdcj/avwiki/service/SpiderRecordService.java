package com.killxdcj.avwiki.service;

import java.util.List;
import java.util.Map;

import com.killxdcj.avwiki.entiy.SpiderRecord;

public interface SpiderRecordService {
	public String insertSpiderRecord(Map<Object, Object> paramMap);
	public void setRecordVisited(String id);
	public List<SpiderRecord> getSpiderRecord(String sign, boolean visited);
}
