package edu.og.moa.board.performance.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.og.moa.board.performance.model.dto.PerformanceBoard;
import edu.og.moa.board.performance.model.dto.PerformanceBoardImage;
import edu.og.moa.board.performance.model.service.PerformanceService;
import edu.og.moa.member.model.dto.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/board/4")
@SessionAttributes("loginMember")
@Slf4j
public class PerformanceController {

	
	@Autowired
	private PerformanceService service;
	
	
	
	// 공연 장르별 목록 조회
	@GetMapping("/pmTypeList")
	public String selectPmTypeList (
			@RequestParam(value = "type", required = false, defaultValue = "all") String type,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			Model model
			) {
		
		// 게시글 목록 조회 서비스 호출
		Map<String, Object> map = service.selectPmTypeList(type, cp);
		
		// 조회 결과를 request scope 에 세팅 후 forward
		model.addAttribute("map", map);
		
		return "board/performance/pm-list";
	}
	
	
	
	
	// 공연 주변지도 목록 조회
	@GetMapping("/pmMap")
	public String selectMap () {
		
		return "board/performance/pm-map";
	}
	
	// 상세 페이지
	@GetMapping("/{boardNo:[0-9]+}")
	public String pmDetail(
			@PathVariable("boardNo") int boardNo,
			Model model,
			RedirectAttributes ra,
			@SessionAttribute(value = "loginMember", required=false) Member loginMember,
			HttpServletRequest req,
			HttpServletResponse resp
			) throws ParseException {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("boardNo", boardNo);
		
		PerformanceBoard board = service.selectPmDetail(map);
		
		System.out.println(board);
		
		String path = null;
		
		if (board != null) {
		
			// 로그인 여부
			if (loginMember != null) {
				
				map.put("memberNo", loginMember.getMemberNo());
				
				// 좋아요 여부 확인
				int result = service.boardLikeCheck(map);
				
				if (result > 0) {
					model.addAttribute("likeCheck", "yes"); // ==> 원하는걸로 작성
					//==> 좋아요를 누른적이 있으면 likeCheck라는 키에 값이 있는채로 가짐
				}
			}
				
			// 쿠키로 조회수 증가
			if (loginMember == null) {
				
				Cookie c = null;
				
				Cookie[] cookies = req.getCookies();
				
				if (cookies != null) {
					
					for (Cookie cookie : cookies) {
						if (cookie.getName().equals("readBoardNo")) {
							c = cookie;
							break;
						}
					}
				}
				
				// 조회수가 늘어나야 하는 경우
				int result = 0;
				
				if (c == null) {
					// 쿠키 존재 X -> 하나 새로 생성
					c = new Cookie("readBoardNo", "|" + boardNo + "|");
					
					// 조회수 증가 서비스 호출
					result = service.updateReadCount(boardNo);
				} else { // 쿠키가 존재함
					
					// 이 게시글 쿠키인지 확인
					if (c.getValue().indexOf("|" + boardNo + "|") == -1) {
						// 쿠키에 현재 게시글 번호가 없다면
						
						// 기존 쿠키 값에 게시글 번호를 추가해서 다시 세팅
						c.setValue(c.getValue() + "|" + boardNo + "|");
						
						// 조회수 증가 서비스 호출
						result = service.updateReadCount(boardNo);
					}
					
				}
				
				// 조회수 증가 성공시
				if (result != 0) {
					// 조회된 board의 조회수와 DB의 조회수 동기화
					
					board.setBoardCount(board.getBoardCount() + 1);
					
					
					// 쿠키 적용 경로 성정
					c.setPath("/"); // "/" 이하 경로 요청 시 쿠키 서버로 전달
					// ==> 모든 요청을 할 때 마다 쿠키가 담기게 된다
					
					// 수명 지정
					// ==> Date로 해도 되지만 너무 오래된 친구라서 최신 문물인 Calendar 사용
					Calendar cal = Calendar.getInstance(); // 싱글톤패턴
					// ==> 싱글톤패턴 : 하나의 객체만을 가지고 코드를 수행하고 있는 것(예시 : dao 반복사용 같은 것)
					cal.add(Calendar.DATE, 1); // ==> 1일을 세팅
					// ==> getInstance : 오늘
					
					
					// 날짜 표기법 변경 객체
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					
					// java.util.Date로 임포트 하기!!
					Date current = new Date(); // 현재 시간
					
					Date temp = new Date(cal.getTimeInMillis()); // 내일 (24시간 후)
					// 예 : 2025-08-28 3:33:33 
					
					Date tmr = sdf.parse(sdf.format(temp)); // 내일 0시 0분 0초
					// 2025-08-28 이 된다
					
					// 내일 0시 0분 0초 - 현재시간 -> 쿠키 수명
					// ==> 내일 0시0분0초에서 현재시간을 빼면 쿠키 수명이 된다
					long diff = (tmr.getTime() - current.getTime()) / 1000;
					// 내일 0시 0분 0초까지 남은 시간을 초 단위로 반환
					
					c.setMaxAge((int)diff); // 수명 설정
					
					resp.addCookie(c); // 응답 객체를 이용해서 클라이언트에게 전달
					
					
				}
			}
			
			path = "board/performance/pm-detail";
			model.addAttribute("board", board);
			
			// 포스터(썸네일)용
			PerformanceBoardImage thumbnail = null;
			
			thumbnail = board.getPmImageList().get(0);
			model.addAttribute("thumbnail", thumbnail);
			
		} else { // 게시글이 없는 경우
			path = "redirect:/board/4/pmTypeList";
			ra.addFlashAttribute("message","해당 게시글이 존재하지 않습니다.");
		}
		
		return path;
	}
	
	
	// 좋아요 처리
	@PostMapping("/like")
	@ResponseBody
	public int like(
			@RequestBody Map<String, Integer> paramMap
			) {
		
		return service.like(paramMap);
	}
	
	// 공연 상세검색 목록 조회
	@GetMapping("/pmSearchList")
	public String selectPmSearchList (
			@RequestParam MultiValueMap<String, String> params,
			Model model
			
			) {
		List<String> type = params.get("type");
	    List<String> price = params.get("price");
	    List<String> date = params.get("date");
	    List<String> address = params.get("address");
	    List<String> text = params.get("text");
		
	    log.debug("type", type);
	    log.debug("type", price);
	    log.debug("type", date);
	    log.debug("type", address);
	    log.debug("type", text);
		
		
		return "board/performance/pm-search";
	}
	
	
	
	
	
	
	
	
}
