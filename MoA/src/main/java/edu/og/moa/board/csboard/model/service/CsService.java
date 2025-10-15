package edu.og.moa.board.csboard.model.service;

import java.util.Map;

import edu.og.moa.board.csboard.model.dto.BoardJtw;

public interface CsService {

	Map<String, Object> selectQuestionList(int communityCode, int qCode, BoardJtw boardJtw, int cp);

}
