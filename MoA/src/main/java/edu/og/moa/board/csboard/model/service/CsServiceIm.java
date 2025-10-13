package edu.og.moa.board.csboard.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.og.moa.board.csboard.model.dao.CsMapper;
import edu.og.moa.board.csboard.model.dto.Board;

@Service
public class CsServiceIm implements CsService{
	
	@Autowired
	public CsMapper mapper;
	

	@Override
	public List<Board> selectQuestionList(int communityCode, int qCode, int cp) {
		
		
		
		return CsMapper;
	}
	
	

}
