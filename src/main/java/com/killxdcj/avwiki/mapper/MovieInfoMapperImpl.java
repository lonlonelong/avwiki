package com.killxdcj.avwiki.mapper;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.killxdcj.avwiki.entiy.MovieInfo;

@Component
public class MovieInfoMapperImpl implements MovieInfoMapper {
	private static final Logger logger = Logger.getLogger(MovieInfoMapperImpl.class);
	
	@Autowired
	private AvwikiSqlSessionFactory avwikiSqlSessionFactory;
	
	public String insertMovieInfo(Map<Object, Object> paramMap) {
		MovieInfo movieInfo = new MovieInfo();
		movieInfo.setNumber((String)paramMap.get("number"));
		movieInfo.setCompany((String)paramMap.get("company"));
		movieInfo.setStudio((String)paramMap.get("studio"));
		movieInfo.setSeries((String)paramMap.get("series"));
		movieInfo.setTitle((String)paramMap.get("title"));
		movieInfo.setPlayer((String)paramMap.get("player"));
		movieInfo.setCategory((String)paramMap.get("category"));
		movieInfo.setLabel((String)paramMap.get("label"));
		movieInfo.setIssuedate((String)paramMap.get("issuedate"));
		movieInfo.setPlaytime((String)paramMap.get("playtime"));
		movieInfo.setComment((String)paramMap.get("comment"));
		movieInfo.setReserve((String)paramMap.get("reserve"));
		return insertMovieInfo(movieInfo);
	}

	public String insertMovieInfo(MovieInfo movieInfo) {
		SqlSession sqlSession = avwikiSqlSessionFactory.getSqlSession(AvwikiSqlSessionFactory.DB_AVWIKI);
		try {
			sqlSession.insert("com.killxdcj.avwiki.movieinfoMapper.insertMovieInfo", movieInfo);
			sqlSession.commit();
		} catch(Exception e) {
			logger.error("MovieInfoMapperImpl : insert data error " + e.getMessage());
		} finally {
			sqlSession.close();
		}
		return movieInfo.getId();
	}
}
