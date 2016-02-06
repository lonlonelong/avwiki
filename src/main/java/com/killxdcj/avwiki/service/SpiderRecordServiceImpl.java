package com.killxdcj.avwiki.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.killxdcj.avwiki.entiy.SpiderRecord;
import com.killxdcj.avwiki.mapper.SpiderRecordMapperImpl;

@Component
public class SpiderRecordServiceImpl implements SpiderRecordService {

	@Autowired
	private SpiderRecordMapperImpl spiderRecordMapper;
	
	public String insertSpiderRecord(Map<Object, Object> paramMap) {
		return spiderRecordMapper.insertSpiderRecord(paramMap);
	}

	public void setRecordVisited(String id) {
		Map<Object, Object> paramMap = new HashMap<Object, Object>();
		paramMap.put("id", id);
		paramMap.put("visited", "y");
		spiderRecordMapper.updateSpiderRecord(paramMap);
	}


	public List<SpiderRecord> getSpiderRecord(String sign, boolean visited) {
		Map<Object, Object> paramMap = new HashMap<Object, Object>();
		paramMap.put("sign", sign);
		if (visited) {
			paramMap.put("visited", "y");
		} else {
			paramMap.put("visited", "n");
		}
		return spiderRecordMapper.selectSpiderRecord(paramMap);
	}

}
