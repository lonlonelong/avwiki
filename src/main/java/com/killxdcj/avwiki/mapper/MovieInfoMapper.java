package com.killxdcj.avwiki.mapper;

import java.util.Map;

import com.killxdcj.avwiki.entiy.MovieInfo;

public interface MovieInfoMapper {
	public String insertMovieInfo(Map<Object, Object> paramMap);
	public String insertMovieInfo(MovieInfo movieInfo);
}
