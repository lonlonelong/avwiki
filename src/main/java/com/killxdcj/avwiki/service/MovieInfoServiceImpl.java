package com.killxdcj.avwiki.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.killxdcj.avwiki.mapper.MovieInfoMapperImpl;

@Component
@Repository("movieInfoService")
public class MovieInfoServiceImpl implements MovieInfoService {

	@Autowired
	private MovieInfoMapperImpl movieInfoMapper;
	
	public String insertMovieInfo(Map<Object, Object> paramMap) {
		return movieInfoMapper.insertMovieInfo(paramMap);
	}

}
