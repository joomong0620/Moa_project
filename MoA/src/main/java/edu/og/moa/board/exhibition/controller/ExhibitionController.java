package edu.og.moa.board.exhibition.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
//import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.expression.ParseException; // 상세조회시 Exception
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.og.moa.board.exhibition.model.dto.BoardImgDB;
import edu.og.moa.board.exhibition.model.dto.Exhibition;
import edu.og.moa.board.exhibition.model.dto.MemberDB;
import edu.og.moa.board.exhibition.model.dto.TicketingInfo;
import edu.og.moa.board.exhibition.model.service.ExhibitionService;
import edu.og.moa.board.exhibition.model.service.JsonExhibitionService;
import edu.og.moa.member.model.dto.Member;
import edu.og.moa.pay.model.dto.Payment;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/board")
@SessionAttributes("loginMember") 
public class ExhibitionController {

	@Autowired
	private ExhibitionService exhibitionService;
	
	@GetMapping("/{communityCode:[3]+}")   
	public String selectExhibitionList(@PathVariable("communityCode") int communityCode
			, @RequestParam(value="cp", required=false, defaultValue ="1") int cp 
			, Model model 
			, @RequestParam Map<String, Object> paramMap 
			) {  
		
		log.info("Exhibition detail communityCode: {}", communityCode); 
		log.info("Exhibition detail cp: {}", cp);		
			
		if (paramMap.get("query") == null ) { // 검색어가 없을 경우 
			
			// 게시글 목록 조회 서비스 호출
			Map<String, Object> map = exhibitionService.selectExhibitionList(communityCode, cp);
			
			// 조회 결과를 request scope에 세팅 후 forward
			model.addAttribute("map", map);
						
		} else { 
			// 검색어가 있을 경우
			paramMap.put("communityCode", communityCode); 
			
			// 검색용 게시글 목록 조회 서비스 호출
			Map<String, Object> map = exhibitionService.selectExhibitionList(paramMap, cp); 
			
			model.addAttribute("map", map);
			
		}
		
		return "board/exhibition/exhibitionList"; 
	}
	
	
	@GetMapping("/{communityCode:[3]+}/{boardNo}")
	public String exhibitionDetail(@PathVariable("communityCode") int communityCode
			, @PathVariable("boardNo") int boardNo
			, Model model 
			, RedirectAttributes ra 
			, @SessionAttribute(value = "loginMember", required=false) Member loginMember
			, HttpServletRequest req
			, HttpServletResponse resp
			) throws ParseException {
		
		Map<String, Object> map = new HashMap<String, Object>(); 
		log.info("Exhibition detail communityCode: {}", communityCode); 
		log.info("Exhibition detail boardNo: {}", boardNo);
		
		map.put("communityCode", communityCode);
		map.put("boardNo", boardNo);
			
		// 게시글 상세 조회 서비스 호출
		Exhibition exhibition = exhibitionService.selectExhibition(map); 
		log.info("Exhibition detail (boardNo= {}): {}", boardNo, exhibition);
		
		String path = null;
		if(exhibition != null) { 

			// 현재 로그인한 상태인 경우
			// 로그인한 회원이 해당 게시글에 좋아요를 눌렀는지 확인
			if (loginMember != null) { 
				// 회원 번호를 기존에 만들어둔 map에 추가
				map.put("memberNo", loginMember.getMemberNo()); 
				
				// 좋아요 여부 확인 서비스 호출
				int result = exhibitionService.exhibitionLikeCheck(map);
				
				// 좋아요를 누른 적이 있을 경우
				if(result > 0) { 
					model.addAttribute("likeCheck", "yes");
				}
			}
			
			//---------------------------------------------------------
			// 쿠키를 이용한 조회수 증가 
			//
			// 1) 비회원 또는 로그인한 회원의 글이 아닌 경우
			if(loginMember == null || 
				loginMember.getMemberNo() != exhibition.getMemberNo()) {
				
				// 2) 쿠키 얻어오기
				Cookie c = null;
				
				// 요청에 담겨있는 모든 쿠키 얻어오기
				Cookie[] cookies = req.getCookies();
				
				// 쿠키가 존재하는 경우
				if(cookies != null) {
					for (Cookie cookie : cookies) {
						if(cookie.getName().equals("readBoardNo")) {
							c = cookie; // 기존에 쿠키가 존재 
							break;
						}
					}
				} 
				
				// 3) 기존에 쿠키가 없거나
				//    존재는 하지만 현재 게시글 번호가 쿠기에 저장되지 않은 경우
				//    (오늘 해당 게시글을 본적이 없는 경우)
				int result = 0; // 결과값 저장 변수
				
				if (c==null) {
					// 쿠키 존재 X -> 하나 새로 생성
					c = new Cookie("readBoardNo", "|" + boardNo + "|");   
					
					// 조회수 증가 서비스 호출
					result = exhibitionService.updateReadCount(boardNo);
					
				} else { // 쿠키가 존재 O : 위에서 찾아 c에 담아 놓은 쿠키
					
					// 현재 게시글 번호가 있는지 확인
					if(c.getValue().indexOf("|" + boardNo + "|") == -1) {
						// 쿠키에 현재 게시글 번호가 없다면					
						// 기존 쿠키 값에 게시글 번호를 추가해서 다시 세팅
						c.setValue(c.getValue() + "|" + boardNo + "|");
						
						// 조회수 증가 서비스 호출
						result = exhibitionService.updateReadCount(boardNo);
					}
				}
				
				// 4) 조회수 증가 성공 시 ( readCount 업데이트 필요)
				//    쿠키가 적용되는 경로, 수명(당일 23시 59분 59초) 지정
				if (result != 0 ) {
					// 조회된 board의 조회수와 DB의 조회수 동기화 
					exhibition.setReadCount(exhibition.getReadCount() + 1);
					
					// [ 쿠키 적용 경로 설정 ]
					c.setPath("/"); // "/" 이하 경로 요청 시 쿠키 서버로 전달
					
					// [ 쿠키 수명 지정 ]
					Calendar cal = Calendar.getInstance();  
					cal.add(Calendar.DATE, 1); // 1일
					
					// 날짜 표기법 변경 객체
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					
					Date current = new Date(); // 현재 시간
					
					Date temp = new Date(cal.getTimeInMillis()); 
					
					Date tmr = sdf.parse(sdf.format(temp)); 
					 
					long diff = (tmr.getTime() - current.getTime()) / 1000; 
					
					c.setMaxAge((int)diff); 
					
					// [ 쿠키를 resp에 담아서 보낸다 ]
					resp.addCookie(c); 
				}
			}
			
			//---------------------------------------------------------
			path = "board/exhibition/exhibitionDetail";
			
			model.addAttribute("exhibition", exhibition); 
			log.info("exhibition: {}", exhibition);
			
			// 게시글 이미지가 있는 경우
			BoardImgDB thumbnail = null; 
			if(!exhibition.getImageList().isEmpty()) {
				
				// 썸네일 == 0번 인덱스 이미지의 순서가 0인 경우
				if(exhibition.getImageList().get(0).getImgOrder() == 0) {
					thumbnail = exhibition.getImageList().get(0); // BoardImage 객체
				}
				
				model.addAttribute("thumbnail", thumbnail); 
			}
			
			model.addAttribute("start", thumbnail != null ? 1 : 0); 
			
			
		} else { 
			path = "redirect:/board/" + communityCode; 
			ra.addFlashAttribute("message",  "해당 게시글이 존재하지 않습니다." ); 
		}
		
		return path;
	}	
	
	

	@PostMapping("/exhibition/submitTicketingInfo")
	public String submitInfo(@ModelAttribute Payment ticketingInfo, Model model) {
	    model.addAttribute("ticketingInfo", ticketingInfo);
	    return "/board/exhibition/submitTicketingInfo"; 
	}	
	
//	// 좋아요 처리
//	@PostMapping("/like")
//	@ResponseBody // 반환되는 값이 비동기 요청한 곳으로 돌아가게 함; AJAX 처리
//	public int like(@RequestBody Map<String, Integer> paramMap) { // Map<k, v> Object대신 Integer로 받으면 down-casting해줄 필요 없음
//		System.out.println(paramMap);
//		
//		//return 0; // 반환값 0가 에러발생시킴 java.lang.IllegalArgumentException: Unknown return value type: java.lang.Integer
//		return service.like(paramMap);
//	}	

}
