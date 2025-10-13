package edu.og.moa.board.csboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import edu.og.moa.board.csboard.model.service.CsService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/board")
@SessionAttributes("loginMember")
@Slf4j
public class CsBoardController {
	
	@Autowired
	private CsService service;
	
	
	// 자주 묻는 질문으로 화면 이동
	@GetMapping("/question")
	public String question() {
		
		return "board/csboard/question";
				
	}
	
	// 내 문의 내역으로 화면 이동
	@GetMapping("/question/questionList")
	public String questionList() {
		return "board/csboard/questionList";
	}
	
	// 내 문의 내역 게시판 조회
	@PostMapping("/question/questionList")
	public String questionList() {
		
		
		
	}

}
