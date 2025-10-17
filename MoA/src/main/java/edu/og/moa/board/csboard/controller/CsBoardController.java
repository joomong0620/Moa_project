package edu.og.moa.board.csboard.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import edu.og.moa.board.csboard.model.dto.BoardJtw;
import edu.og.moa.board.csboard.model.service.CsService;
import edu.og.moa.member.model.dto.Member;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/board")
@SessionAttributes("loginMember")
@Slf4j
public class CsBoardController {
	
	@Autowired
	private CsService service;
	
	
	// 자주 묻는 질문으로 화면 이동
	@GetMapping("/{communityCode:5}")
	public String question(Model model, @PathVariable("communityCode") int communityCode, 
			@RequestParam(value="qCode",  defaultValue="1") int qCode
			
			) {
	    model.addAttribute("communityCode", communityCode); // 변수 추가
	    model.addAttribute("qCode", qCode); // 기본 문의 유형 지정 (예시)
	    
	    
	   System.out.println("qCode : " + qCode);
	    
	    return "board/csboard/question";
	}
	
	// 내 문의 내역 게시판 조회
	@GetMapping("/{communityCode:5}/questionList/{qCode:[1-3]}")
	public String questionList(@PathVariable("communityCode") int communityCode,
			@RequestParam(value="cp", required=false, defaultValue="1") int cp,
			@PathVariable("qCode") int qCode, Model model, BoardJtw boardJtw,
			@RequestParam(value="boardNo") int boardNo,
			@SessionAttribute("loginMember") Member loginMember
			
			
			) {
		
		boardJtw.setBoardNo(boardNo);

		boardJtw.setCommunityCode(communityCode);
		boardJtw.setQCode(qCode);
		boardJtw.setMemberNo(loginMember.getMemberNo());
		
		System.out.println(communityCode);
		
		
		
		Map<String, Object> map = service.selectQuestionList(communityCode, qCode, boardJtw, cp);
		
		model.addAttribute("communityCode", communityCode);
		model.addAttribute("qCode", qCode);
		
		model.addAttribute("questionList", map.get("questionList"));
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("qCodeList", map.get("qCodeList"));

		System.out.println("communityCode" + communityCode);
		System.out.println("qCode" + qCode);
		System.out.println("boardJtw" + boardJtw);
		System.out.println("cp" + cp);
		System.out.println("qCodeList : " + map.get("qCodeList"));
		System.out.println("questionList : " + map.get("questionList"));
		
		
		
		
		
		return "board/csboard/questionList";
	}
	
	
		
		
		
		
		
}


