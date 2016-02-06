package com.killxdcj.avwiki.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

public class AvwikiSqlSessionFactory {
	private static final Logger logger = Logger.getLogger(AvwikiSqlSessionFactory.class);
	
	public static final String DB_AVWIKI = "db_avwiki";
	public static final String DB_AVWIKI_CONF = "avwiki_mybatis_conf.xml";
	
	private static Map<String, SqlSessionFactory> sqlSessionFactoryMap = new HashMap<String, SqlSessionFactory>();
	
	static {
		sqlSessionFactoryMap.put(DB_AVWIKI, getSqlSessionFactory(DB_AVWIKI_CONF));
	}
	
	public SqlSession getSqlSession(String db) {
		try {
			return sqlSessionFactoryMap.get(db).openSession();
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}
	}
	
	private static SqlSessionFactory getSqlSessionFactory(String mybatisConf) {
		try {
			InputStream inputStream = Resources.getResourceAsStream(mybatisConf);
			return new SqlSessionFactoryBuilder().build(inputStream);
		} catch (IOException e) {
			logger.error(e.toString());
			return null;
		}
		
	}
}
