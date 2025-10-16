package edu.og.moa.board.csboard.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.og.moa.board.csboard.model.dto.BoardJtw;

@Mapper
public interface CsMapper {

		int getListCount(Map<String, Object> paramMap); 
		
		List<BoardJtw> selectQuestionList(BoardJtw boardJtw);
	

}
