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
	private FreeBoardService service;

	// 게시글 목록 조회
	@GetMapping("/{boardCode:1}")
	public String selectBoardList(
			@PathVariable("boardCode") int boardCode,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp, 
			@RequestParam(value = "testMemberNo", required = false) Integer testMemberNo,
			Model model,
			@RequestParam Map<String, Object> paramMap,
			HttpSession session) {
	  

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
		return "board/freeboard/freeboardList";
	}

	// 게시글 상세 조회
	@GetMapping("/{boardCode:1}/{boardNo:[0-9]+}")
	public String selectBoardDetail(
			@PathVariable("boardCode") int boardCode, 
			@PathVariable("boardNo") int boardNo,
			@RequestParam(value = "testMemberNo", required = false) Integer testMemberNo,
			Model model, 
			@SessionAttribute(value = "loginMember", required = false) Member loginMember,
			RedirectAttributes ra, 
			HttpServletRequest req, 
			HttpServletResponse resp,
			HttpSession session) throws ParseException {

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

			// 쿠키를 이용한 조회수 증가
			if (loginMember == null || loginMember.getMemberNo() != board.getMemberNo()) {
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

				int result = 0;

				if (c == null) {
					c = new Cookie("readBoardNo", "|" + boardNo + "|");
					result = service.updateReadCount(boardNo);
				} else {
					if (c.getValue().indexOf("|" + boardNo + "|") == -1) {
						c.setValue(c.getValue() + "|" + boardNo + "|");
						result = service.updateReadCount(boardNo);
					}
				}

				if (result != 0) {
					board.setBoardCount(board.getBoardCount() + 1);
					c.setPath("/");

					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DATE, 1);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date current = new Date();
					Date temp = new Date(cal.getTimeInMillis());
					Date tmr = sdf.parse(sdf.format(temp));
					long diff = (tmr.getTime() - current.getTime()) / 1000;
					c.setMaxAge((int) diff);
					resp.addCookie(c);
				}
			}

			path = "board/freeboard/freeboardDetail";
			model.addAttribute("board", board);

			BoardImage thumbnail = null;
			if (board.getImageList() != null && !board.getImageList().isEmpty()) {
				if (board.getImageList().get(0).getImgOrder() == 0) {
					thumbnail = board.getImageList().get(0);
				}
				model.addAttribute("thumbnail", thumbnail);
			}

			model.addAttribute("start", thumbnail != null ? 1 : 0);

		} else {
			path = "redirect:/board/" + boardCode;
			ra.addFlashAttribute("message", "해당 게시글이 존재하지 않습니다.");
		}

		return path;
	}

	// 좋아요 처리
	@PostMapping("/like")
	@ResponseBody
	public int like(@RequestBody Map<String, Integer> paramMap, HttpSession session) {
		
		// 좋아요는 로그인 필수이므로 세션 체크
		Member loginMember = (Member) session.getAttribute("loginMember");
		if(loginMember == null) {
			throw new RuntimeException("로그인이 필요합니다.");
		}
		
		return service.like(paramMap);
	}
}