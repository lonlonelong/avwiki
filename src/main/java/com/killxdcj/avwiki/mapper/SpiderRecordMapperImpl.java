package com.killxdcj.avwiki.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.killxdcj.avwiki.entiy.SpiderRecord;

@Component
public class SpiderRecordMapperImpl implements SpiderRecordMapper {

	@Autowired
	private AvwikiSqlSessionFactory avwikiSqlSessionFactory;
	
	public String insertSpiderRecord(Map<Object, Object> paramMap) {
		SpiderRecord spiderRecord = new SpiderRecord();
		spiderRecord.setSign(paramMap.get("sign").toString());
		spiderRecord.setUrl(paramMap.get("url").toString());
		return insertSpiderRecord(spiderRecord);
	}

	public String insertSpiderRecord(SpiderRecord spiderRecord) {
		SqlSession sqlSession = avwikiSqlSessionFactory.getSqlSession(AvwikiSqlSessionFactory.DB_AVWIKI);
		try {
			sqlSession.insert("com.killxdcj.avwiki.spiderrecordMapper.insertSpiderRecord", spiderRecord);
			sqlSession.commit();
		} finally {
			sqlSession.close();
		}
		return spiderRecord.getId();
	}

	public int updateSpiderRecord(Map<Object, Object> paramMap) {
		SpiderRecord spiderRecord = new SpiderRecord();
		spiderRecord.setId((String)paramMap.get("id"));
		spiderRecord.setIsDelete((String)paramMap.get("isDelete"));
		spiderRecord.setVisited((String)paramMap.get("visited"));
		spiderRecord.setSign((String)paramMap.get("sign"));
		spiderRecord.setUrl((String)paramMap.get("url"));
		return updateSpiderRecord(spiderRecord);
	}

	public int updateSpiderRecord(SpiderRecord spiderRecord) {
		int affected = 0;
		SqlSession sqlSession = avwikiSqlSessionFactory.getSqlSession(AvwikiSqlSessionFactory.DB_AVWIKI);
		try {
			affected = sqlSession.update("com.killxdcj.avwiki.spiderrecordMapper.updateSpiderRecord", spiderRecord);
			sqlSession.commit();
		} finally {
			sqlSession.close();
		}
		return affected;
	}
	
	public List<SpiderRecord> selectSpiderRecord(Map<Object, Object> paramMap) {
		List<SpiderRecord> spiderRecords = null;
		SqlSession sqlSession = avwikiSqlSessionFactory.getSqlSession(AvwikiSqlSessionFactory.DB_AVWIKI);
		try {
			spiderRecords = sqlSession.selectList("com.killxdcj.avwiki.spiderrecordMapper.selectSpiderRecord", paramMap);
		} finally {
			sqlSession.close();
		}
		return spiderRecords;
	}
}
