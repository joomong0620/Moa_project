package edu.og.moa.board.csboard.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.og.moa.board.csboard.model.dao.CsMapper;
import edu.og.moa.board.csboard.model.dto.BoardJtw;
import edu.og.moa.board.csboard.model.dto.PaginationJtw;
import edu.og.moa.board.csboard.model.dto.Question;

@Service
public class CsServiceIm implements CsService{
	
	@Autowired
	public CsMapper mapper;
	

	// 삭제되지 않은 게시글 목록 조회
	@Override
	public Map<String, Object> selectQuestionList(int communityCode, int qCode, BoardJtw boardJtw, int cp) {
		
		Map<String, Object> paramMap = new HashMap();
		
		paramMap.put("communityCode", communityCode);
		paramMap.put("qCode", qCode);
		
		System.out.println("communityCode1 :" + communityCode);
		
		int listCount = mapper.getListCount(paramMap);
		
		System.out.println("communityCode2 :" + communityCode);
		
		PaginationJtw pagination = new PaginationJtw(cp, listCount);
		
		// offset 계산. offset = 한 번에 뛰어넘을 게시글 숫자.
		// cp=1 이고 limit=10 이면 offset = 0 -> 0번째 부터 10개를 뛰어넘음. index라서 0번째는 1번 게시글임.
		// cp=2 이고 limit=10 이면 offset = 10  -> 10번째 부터 10개를 뛰어넘음. 
		int offset = (pagination.getCurrentPage() -1)*pagination.getLimit();
		
		paramMap.put("limit", pagination.getLimit());
		paramMap.put("offset", offset);
		
		boardJtw.setCommunityCode(communityCode);
		boardJtw.setQCode(qCode);
		System.out.println("communityCode3 :" + communityCode);
		
		List<Question> qCodeList = mapper.selectQCodeList(qCode);
		
		System.out.println("qCodeList : " + qCodeList);
		
		
		List<BoardJtw> questionList = mapper.selectQuestionList(boardJtw);
		
		System.out.println("boardJtw :" + boardJtw);
		System.out.println("communityCode4 :" + communityCode);
		
		paramMap.put("qCodeList", qCodeList);
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("pagination", pagination);
		map.put("questionList", questionList);
		
		
		
		return map;
	}
	
	

}
