package edu.og.moa.board.csboard.model.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CsMapper {

		int getListCount(Map<String, Object> paramMap); 
		
		Map<String, Object> selectQuestionList(Map<String, Object> paramMap);
	

}
