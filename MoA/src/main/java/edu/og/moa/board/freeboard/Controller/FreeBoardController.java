package edu.og.moa.board.freeboard.Controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

import edu.og.moa.board.freeboard.model.dto.Board;
import edu.og.moa.board.freeboard.model.dto.BoardImage;
import edu.og.moa.board.freeboard.model.service.FreeBoardService;
import edu.og.moa.member.model.dto.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/board")
@SessionAttributes("loginMember")
public class FreeBoardController {

	@Autowired
	private FreeBoardService service; // 이름은 FreeBoardService지만, 사실상 전체 게시판 조회에 사용

	// 게시글 목록 조회
	@GetMapping("/{boardCode:[0-9]+}")
	public String selectBoardList(@PathVariable("boardCode") int boardCode,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp, Model model,
			@RequestParam Map<String, Object> paramMap,
			HttpSession session) {
	   
		
		// 테스트용 로그인 회원 세션 생성
	    if (session.getAttribute("loginMember") == null) {
	        Member fakeMember = new Member();
	        fakeMember.setMemberNo(3);
	        fakeMember.setMemberNickname("유저일");
	        session.setAttribute("loginMember", fakeMember);
	    }

		Map<String, Object> map = service.selectFreeBoardList(boardCode, cp);
		model.addAttribute("map", map);
		model.addAttribute("boardCode", boardCode);

		System.out.println("=== 게시판 종류 ===");
		service.selectBoardTypeList().forEach(row -> System.out.println(row));

		System.out.println("=== 게시판 코드 " + boardCode + " 글 목록 ===");
		if (map.get("boardList") != null) {
			((java.util.List<?>) map.get("boardList")).forEach(System.out::println);
		} else {
			System.out.println("게시글 없음");
		}
		return "board/freeboard/freeboardList"; // 뷰 이름은 공통으로

	}

	// 게시글 상세 조회
	@GetMapping("/{boardCode:[0-9]+}/{boardNo:[0-9]+}")
	public String selectBoardDetail(@PathVariable("boardCode") int boardCode, @PathVariable("boardNo") int boardNo,
			Model model, @SessionAttribute(value = "loginMember", required = false) Member loginMember,
			RedirectAttributes ra, HttpServletRequest req, HttpServletResponse resp) throws ParseException {

		// 1️ 전달용 map에 기본정보 담기
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);

		// 2️ 서비스 호출 → 게시글 상세정보 가져오기
		Board board = service.selectFreeBoardDetail(map);

		String path = null;
		// 조회 결과가 있는 경우
		if (board != null) {

			if (loginMember != null) {
				map.put("memberNo", loginMember.getMemberNo());

				// 좋아요 여부 확인 서비스 호출
				int result = service.boardLikeCheck(map);

				// 좋아요를 누른 적이 있을 경우
				if (result > 0)
					model.addAttribute("likeCheck", "yes");

			}
			// --------------------------------------------------------------
			// ---------------------------------------------------
			// 쿠키를 이용한 조회수 증가

			// 1) 비회원 또는 로그인한 회원의 글이 아닌 경우
			if (loginMember == null || loginMember.getMemberNo() != board.getMemberNo()) {

				// 2) 쿠키 얻어오기
				Cookie c = null;

				// 요청에 담겨있는 모든 쿠키 얻어오기
				Cookie[] cookies = req.getCookies();

				// 쿠키가 존재하는 경우
				if (cookies != null) {

					// 쿠키 중 "readBoardNo" 쿠키를 찾아서 c에 대입
					for (Cookie cookie : cookies) {
						if (cookie.getName().equals("readBoardNo")) {
							c = cookie;
							break;
						}
					}

				}
				// 3) 기존 쿠키가 없거나
				// 존재는 하지만 현재 게시글 번호가 쿠키에 저장되지 않은 경우
				// (오늘 해당 게시글을 본 적이 없는 경우)

				int result = 0;

				if (c == null) {
					// 쿠키 존재 X -> 하나 새로 생성
					c = new Cookie("readBoardNo", "|" + boardNo + "|");

					// 조회수 증가 서비스 호출
					result = service.updateReadCount(boardNo);

				} else { // 쿠키 존재 O

					if (c.getValue().indexOf("|" + boardNo + "|") == -1) {
						// 쿠키에 현재 게시글 번호가 없다면

						// 기존 쿠키 값에 게시글 번호를 추가해서 다시 세팅
						c.setValue(c.getValue() + "|" + boardNo + "|");

						// 조회수 증가 서비스 호출
						result = service.updateReadCount(boardNo);
					}

				}
				// 4) 조회수 증가 성공 시
				// 쿠키가 적용되는 경로, 수명(당일 23시 59분 59초) 지정

				if (result != 0) {
					// 조회된 board의 조회수와 DB의 조회수 동기화
					board.setBoardCount(board.getBoardCount() + 1);

					// 쿠키 적용 경로 설정
					c.setPath("/"); // "/" 이하 경로 요청 시 쿠키 서버로 전달

					// 수명 지정
					Calendar cal = Calendar.getInstance(); // 싱글톤패턴
					cal.add(Calendar.DATE, 1);

					// 날짜 표기법 변경 객체
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

					// java.util.Date
					Date current = new Date(); // 현재 시간

					Date temp = new Date(cal.getTimeInMillis()); // 내일 (24시간 후)
					// 2025-08-28 3:33:33

					Date tmr = sdf.parse(sdf.format(temp)); // 내일 0시 0분 0초
					// 2025-08-28

					// 내일 0시 0분 0초 - 현재 시간 -> 쿠키 수명
					long diff = (tmr.getTime() - current.getTime()) / 1000;
					// 내일 0시 0분 0초까지 남은 시간을 초단위로 반환

					c.setMaxAge((int) diff); // 수명 설정

					resp.addCookie(c); // 응답 객체를 이용해서 클라이언트에게 전달
				}
			}
			// ---------------------------------------------------
			path = "board/freeboard/freeboardDetail"; // boardDetail 페이지로 forward
			model.addAttribute("board", board);

			// 게시글 이미지가 있는 경우
			BoardImage thumbnail = null;

			if (board.getImageList() != null && !board.getImageList().isEmpty()) {

				// 썸네일 == 0번 인덱스 이미지의 순서가 0인 경우
				if (board.getImageList().get(0).getImgOrder() == 0) {
					thumbnail = board.getImageList().get(0);
				}
				model.addAttribute("thumbnail", thumbnail); // 썸네일이 없는 경우 null
			
			
			
			}

			// 썸네일이 있으면 start = 1
			// 썸네일이 없으면 start = 0
			model.addAttribute("start", thumbnail != null ? 1 : 0);

		} else { // 없는 경우
			path = "redirect:/board/" + boardCode; // 해당 게시판 목록 첫 페이지로 redirect
			ra.addFlashAttribute("message", "해당 게시글이 존재하지 않습니다.");
		}

		return path;

	}

	// 좋아요 처리
	@PostMapping("/like")
	@ResponseBody
	public int like(@RequestBody Map<String, Integer> paramMap,
			HttpSession session) {
		
		// 테스트용 로그인 회원 세션 생성
	    if (session.getAttribute("loginMember") == null) {
	        Member fakeMember = new Member();
	        fakeMember.setMemberNo(3);
	        fakeMember.setMemberNickname("유저일");
	        session.setAttribute("loginMember", fakeMember);
	    }
		return service.like(paramMap);
	}

}
