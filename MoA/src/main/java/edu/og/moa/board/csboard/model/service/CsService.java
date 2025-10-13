package edu.og.moa.board.csboard.model.service;

import java.util.List;

import edu.og.moa.board.csboard.model.dto.Board;

public interface CsService {

	List<Board> selectQuestionList(int communityCode, int qCode, int cp);

}
