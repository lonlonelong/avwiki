package com.killxdcj.avwiki.mapper;

import java.util.Map;

import com.killxdcj.avwiki.entiy.SpiderRecord;

public interface SpiderRecordMapper {
	public String insertSpiderRecord(Map<Object, Object> paramMap);
	public String insertSpiderRecord(SpiderRecord spiderRecord);
	public int updateSpiderRecord(Map<Object, Object> paramMap);
	public int updateSpiderRecord(SpiderRecord spiderRecord);
}
