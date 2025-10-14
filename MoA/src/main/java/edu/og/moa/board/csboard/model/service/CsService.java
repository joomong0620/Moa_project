package edu.og.moa.board.csboard.model.service;

import java.util.Map;

public interface CsService {

	Map<String, Object> selectQuestionList(int communityCode, int qCode, int cp);

}
