package edu.og.moa.board.csboard.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.og.moa.board.csboard.model.dto.BoardJtw;
import edu.og.moa.board.csboard.model.dto.Question;

@Mapper
public interface CsMapper {

		int getListCount(Map<String, Object> paramMap); 
		
		List<BoardJtw> selectQuestionList(BoardJtw boardJtw);

		List<Question> selectQCodeList(int qCode);
	

}
