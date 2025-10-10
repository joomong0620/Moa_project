package edu.og.moa.board.performance.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;


import edu.og.moa.board.performance.model.service.PerformanceService;

@Controller
@RequestMapping("/board/4")
@SessionAttributes("loginMember")
public class PerformanceController {

	
	@Autowired
	private PerformanceService service;
	
	
	
	// 공연 장르별 목록 조회
	@GetMapping("/pmTypeList")
	public String selectPmTypeList (
			@RequestParam(value = "type", required = false, defaultValue = "all") int type,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			Model model
			) {
		
		Map<String, Object> map = service.selectPmTypeList(type, cp);
		
		model.addAttribute("map", map);
		
		return "board/performance/pm-list";
	}
	
	// 공연 상세검색 목록 조회
	@GetMapping("/pmSearchList")
	public String selectPmSearchList (
//			@RequestParam(value = "type", required = false, defaultValue = "all") int type,
//			@RequestParam(value = "price", required = false, defaultValue = "all") String price,
//			@RequestParam(value = "date", required = false, defaultValue = "all") String date,
//			@RequestParam(value = "address", required = false, defaultValue = "all") String address,
//			
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			Model model,
			@RequestParam Map<String, Object> paramMap
			) {
		
		
		return "board/performance/pm-search";
	}
	
	
	
	
	
	
}
