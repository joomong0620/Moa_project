package edu.og.moa.board.exhibition.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
//import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.expression.ParseException; // 상세조회시 Exception
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.og.moa.board.exhibition.model.dto.BoardImgDB;
import edu.og.moa.board.exhibition.model.dto.Exhibition;
import edu.og.moa.board.exhibition.model.dto.JsonBoardImage;
import edu.og.moa.board.exhibition.model.dto.JsonMember;
import edu.og.moa.board.exhibition.model.dto.MemberDB;
import edu.og.moa.board.exhibition.model.service.ExhibitionService;
import edu.og.moa.board.exhibition.model.service.JsonExhibitionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
//@RequestMapping("/board")
@RequestMapping("/exhibition")
@SessionAttributes("loginMember") 
public class ExhibitionController {

	@Autowired
	private ExhibitionService exhibitionService;
	
	//boardCode === exhibitionCode === communityCode
	//                                 communityCode
	//boardName === exhibitionName === communityName
	
	//게시글 목록 조회 ==> "/board/3" 요청주소 처리 // GET-화면전환, forward
	@GetMapping("/board/{communityCode:[3]+}")   // 임의의 변수이름 지정: communityCode ex) ==>  /board/1(자유게시판),  /board/2(리뷰게시판), /board/3(전시게시판)  ==> /board/like면 boardCode="like"가 들어가며 맵핑되어버린다.  
	//@GetMapping("/{communityCode:[3]+}")   // 임의의 변수이름 지정: communityCode ex) ==>  /board/1(자유게시판),  /board/2(리뷰게시판), /board/3(전시게시판)  ==> /board/like면 boardCode="like"가 들어가며 맵핑되어버린다.  
	                              // communityCode 는 int 인지 String인지 모르고, 끝에 하나를 무조건 받아오는 변수
	public String selectExhibitionList(@PathVariable("communityCode") int communityCode
			, @RequestParam(value="cp", required=false, defaultValue ="1") int cp // required = false ==> cp값 없을수도 있다, 그때 default값은 "1"
			, Model model // 데이터 전달용 모델 객체
			, @RequestParam Map<String, Object> paramMap // 전달 받은 파라미터들 맵에 담아 전달(어차피 DAO에서 전달 파라미터는 하나만 가능; DTO에 못담으면 Map에 담아 전달)
														 // boardList.jsp에 form-tag에서 key 와 query를 입력파라미터로 받아온다
			) {  //  /board/1 ==> @GetMapping("/{communityCode}") ==> @PathVariable("communityCode") ==> communityCode=1 
		// @PathVariable : URL 경로에 있는 값을 매개변수로 이용할 수 있게 하는 어노테이션
		//                 + request scope에 값 set까지 해준다. 세팅해주는 이름은 "/{communityCode}"와 같다 => communityCode
		// @RequestParam(value="cp", required=false, defaultValue = "1"): 맨처음 페이지에는 cp값 없다(그러므로 처음 cp값은 자동으로 = 1; 이후부터 cp값 존재: 2, 3,  4, ..., )
		
		// communityCode 확인
		System.out.println("communityCode : " + communityCode);
		System.out.println("cp : " + cp);
		
		// 게시글 목록 조회 에서 '검색' 기능 구현 (2025/09/04)
		if (paramMap.get("query") == null ) { // 검색어가 없을 경우 // paramMap.get("key") == null 써도 됨
			
			// 게시글 목록 조회 서비스 호출
			Map<String, Object> map = exhibitionService.selectExhibitionList(communityCode, cp);
			
			// 조회 결과를 request scope에 세팅 후 forward
			model.addAttribute("map", map); //model : spring에서 사용하는 데이터 전달 객체 => js에서 이걸 받아 사용 (@PathVariable에 담긴 communityCode와 cp도 담겨져 넘어감)
			
			
			// 로그인 서비스 mock:		
			MemberDB loginMember = new MemberDB();
			loginMember.setMemberNickname("한국문화정보원");
			loginMember.setProfileImg("/images/board/exhibition/member/penguin.jpeg"); 
			loginMember.setMemberNo(10); // 임의할당 for testing (cf:  전시 exhibitionCode === boarcCode ===  communityCode = 3)
			model.addAttribute("loginMember", loginMember); // 
			
		} else { 
			// 검색어가 있을 경우
			
			// 필요한 값 : key, query, communityCode
			paramMap.put("boardCode", communityCode); // @RequestParam에 의해 paramMap에 key, query입력 파라미터는 이미 담겨져 있다.
			
			// 검색용 게시글 목록 조회 서비스 호출
			Map<String, Object> map = exhibitionService.selectExhibitionList(paramMap, cp); // overloading; 따라서 넘겨주는 파라미터들 = paramMap(key, query, communityCode) + cp
			
			model.addAttribute("map", map);
			
			// 로그인 서비스 mock:		
			MemberDB loginMember = new MemberDB();
			loginMember.setMemberNickname("한국문화정보원");
			loginMember.setProfileImg("/images/board/exhibition/member/penguin.jpeg"); 
			loginMember.setMemberNo(10); // 임의할당 for testing (cf:  전시 exhibitionCode === boarcCode ===  communityCode = 3)
			model.addAttribute("loginMember", loginMember); // 			
		}
		
		
		
		//return null;
		return "board/exhibition/exhibitionList"; // 요청주소 (servlet-context.xml의 viewResolver의 prefix와 suffix를 고려한 주소: /WEB-INF/views/  board/exhibition/exhibitionList  .jsp)
								  // forward	
	}
	
	
	// 게시글 상세 조회 (Get방식)  -> /board/3/1500
	// @PathVariable : 주소에 지정된 부분을 변수에 저장 + request scope 세팅
	@GetMapping("/board/{communityCode}/{boardNo}")
	//@GetMapping("/{communityCode}/{boardNo}")
	public String exhibitionDetail(@PathVariable("communityCode") int communityCode
			, @PathVariable("boardNo") int boardNo
			, Model model // 데이터 전달용 객체 
			, RedirectAttributes ra // 리다이렉트 시 데이터 전달용 객체
			//, @SessionAttribute(value = "loginMember", required=false) MemberDB loginMember
			// 세션에서 loginMember를 얻어오는데 없으면 null, 있으면 회원 정보 저장 (로그인 안하고 하트누를 수도 있으므로 required=false)
			
			// 쿠키를 이용한 조회수 증가 시 사용
			, HttpServletRequest req
			, HttpServletResponse resp
			) throws ParseException {// ParseException {
		
		Map<String, Object> map = new HashMap<String, Object>(); 
		log.info("Exhibition detail communityCode: {}", communityCode); // ok
		log.info("Exhibition detail boardNo: {}", boardNo);
		
		map.put("communityCode", communityCode);
		map.put("boardNo", boardNo);
		
		
		// 로그인 서비스 mock:		
		MemberDB loginMember = new MemberDB();
		loginMember.setMemberNickname("한국문화정보원");
		loginMember.setProfileImg("/images/board/exhibition/member/penguin.jpeg"); 
		loginMember.setMemberNo(10); // 임의할당 for testing (cf:  전시 exhibitionCode === boarcCode ===  communityCode = 3)
		model.addAttribute("loginMember", loginMember); // 	--> 이거 밑에서 해주는 거랑은 다르기에 여기서 필요
		
		// 게시글 상세 조회 서비스 호출
		Exhibition exhibition = exhibitionService.selectExhibition(map); // js에 전달되어 화면에 보여지는 변수들 모두 담고 있는 객체
		//System.out.println(exhibition); //출력 확인
		
		log.info("Exhibition detail (boardNo= {}): {}", boardNo, exhibition);
		
		String path = null;
		if(exhibition != null) { // 조회 결과가 있는 경우 (게시글 목록에서 제목 클릭한 상태에서)
			
			
		
			
			//---------------------------------------------------------
			//좋아요 "하트" -> BOARD_LIKE 테이블에 있는것이 하트 누른것(또누르면 해제 -> DB삭제)
			// 현재 로그인한 상태인 경우
			// 로그인한 회원이 해당 게시글에 좋아요를 눌렀는지 확인
			
			if (loginMember != null) { // boardNo, memberNo
				// 회원 번호를 기존에 만들어둔 map에 추가
				map.put("memberNo", loginMember.getMemberNo()); // 담아 가서 필요없으면 않쓰면 됨
				
				// 좋아요 여부 확인 서비스 호출
				int result = exhibitionService.exhibitionLikeCheck(map);
				
				// 좋아요를 누른 적이 있을 경우
				if(result > 0) { // 화면에 하트 보여주기위해  html에 누른적 있는지 알려주기 위해 Model 전달객체 사용
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
					
					// 쿠키 중 "readBoardNo" 이름을 가진 쿠키를 찾아서 c에 대입
					for (Cookie cookie : cookies) {
						if(cookie.getName().equals("readBoardNo")) {
							c = cookie; // 기존에 쿠키가 존재 하면 그거 그냥 가져다 쓴다.
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
					// 1,2,3,4   1/2/3/4   톰캣 8.5이상부터는 쿠키에 , /, 사용 불가 -> 따라서 우리만의 구분자 필요  
					
					// 조회수 증가 서비스 호출
					result = exhibitionService.updateReadCount(boardNo);
					
				} else { // 쿠키가 존재 O : 위에서 찾아 c에 담아 놓은 쿠키
					// 현재 게시글 번호가 있는지 확인
					// cookie.getValue() : 쿠키에 저장된 모든 값을 읽어와서 String으로 반환
					
					// String.indexOf("문자열")
					// -> 찾는 문자열이 몇번 째 인덱스에 존재하는지 반환
					//    단, 없는 경우 -1 반환
					
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
					// (DB에서 update된 조회수 값 가져오거나, 
					// 우리가 한것은 조회수 + 1 한 것이므로 ->)
					exhibition.setReadCount(exhibition.getReadCount() + 1);
					
					// [ 쿠키 적용 경로 설정 ]
					c.setPath("/"); // "/" 이하 경로 요청 시 쿠키 서버로 전달 (모든 요청할 때 마다 쿠키가 담긴다)
					
					// [ 쿠키 수명 지정 (Date보다 Calendar가 개선된 시간관련 클래스) ]
					// java.util.Calendar
					Calendar cal = Calendar.getInstance();  // 클래스명.메소드명 -> static 메소드!
															// 싱글톤 패턴 (하나의 객체만 생성해서 계속 쓰는것) -> ex) 우리의 경우 dao 가 싱클톤패턴
					cal.add(Calendar.DATE, 1); // 1일
					
					// 날짜 표기법 변경 객체
					// java.util.SimpleDateFormat
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					
					// java.util.Date
					Date current = new Date(); // 현재 시간
					
					Date temp = new Date(cal.getTimeInMillis()); // 내일 (24시간 후, 앞에서 1일 더했으므로)
					// 2025-08-28 3:33:33
					
					Date tmr = sdf.parse(sdf.format(temp)); // temp를 "yyyy-MM-dd" 형식으로 바꿔줌 // 내일 0시 0분 0초
					// 2025-08-28
					 
					// 내일 0시 0분 0초 - 현재 시간 -> 쿠키 수명
					long diff = (tmr.getTime() - current.getTime()) / 1000; // .getTime() 반환형이 long 타입
					// 내일 0시 0분 0초까지 남은 시간을 초단위로 반환
					
					c.setMaxAge((int)diff); //.setMaxAge 파라미터는 int이므로 강제 형변환
					
					// [ 쿠키를 resp에 담아서 보낸다 ]
					resp.addCookie(c); // 응답 객체를 이용하여 클라이언트에게 전달
					
				}
			
				
			}
			
			
			//---------------------------------------------------------
			//path = "board/3/exhibitionDetail"; // exhibitionDetail 페이지로 forward 위임 (jsp에게 화면 만들라고 위임 b/c jsp(html에서 java사용)가 servlet(java에서 html구성)보다 화면만들기 쉬움)
										// 처음화면 만들어서 보여주기
			path = "board/exhibition/exhibitionDetail";
			
			model.addAttribute("exhibition", exhibition); // model => forward 하겠다 (request scope)
			
			// 게시글 이미지가 있는 경우
			BoardImgDB thumbnail = null; 
			if(!exhibition.getImageList().isEmpty()) {
				
				// 썸네일 == 0번 인덱스 이미지의 순서가 0인 경우
				if(exhibition.getImageList().get(0).getImgOrder() == 0) {
					thumbnail = exhibition.getImageList().get(0); // BoardImage 객체
				}
				
				model.addAttribute("thumbnail", thumbnail); // 썸네일이 없는 경우 thumbnail=null -> exhibitionDetail.html에서 이제 thumbnail쓸수 있다.
						
			}
			
			// 썸네일이 있으면 start = 1
			// 썸네일이 없으면 start = 0
//				int start = 0;
//				if (thumbnail != null) {
//					start = 1;
//					model.addAttribute("start", 1);
//				} else {
//					model.addAttribute("start", 0);
//				}
			model.addAttribute("start", thumbnail != null ? 1 : 0); // 삼항 연산자로 더 간단히
			
			
		} else { // 없는 경우
			path = "redirect:/board/" + communityCode; // 해당 게시판 목록 첫 페이지로 redirect (==> 이미 만들어진 것 다시 보여주기)
			//model.addAttribute("message", "해당 게시글이 존재하지 않습니다."); // Model객체는 request scope에 존재하므로 -> session에 올려야 한다 by ra
			ra.addFlashAttribute("message",  "해당 게시글이 존재하지 않습니다." ); // ra => redirect 하겠다
		}
		
		return path;
		//return "board/exhibition/exhibitionDetail"; 
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
	
	
<<<<<<< HEAD
=======
	// 게시글 통합 검색 목록 조회(모든 게시판) // 요청주소예시) http://localhost/board/search?query=100&key=all
	@GetMapping("/search")
	public String selectBoardList(
			@RequestParam(value="cp", required=false, defaultValue="1") int cp
			, @RequestParam Map<String, Object> paramMap
			, Model model // 데이터 전달용 모델 객체	
			) {
		
		// 여기서 boardCode
		
		
		// 게시글 통합 검색 목록 조회 서비스 호출
		Map<String, Object> map = exhibitionService.selectExhibitionList(paramMap, cp);
		
		model.addAttribute("map", map);
		
		return "board/boardSearchList";
	}	
	
	
>>>>>>> c701832 (minor change)
}
