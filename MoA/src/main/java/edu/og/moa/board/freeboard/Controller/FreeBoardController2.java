package edu.og.moa.board.freeboard.Controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.og.moa.board.freeboard.model.dto.Board;
import edu.og.moa.board.freeboard.model.service.FreeBoardService;
import edu.og.moa.board.freeboard.model.service.FreeBoardService2;
import edu.og.moa.member.model.dto.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/board2")
public class FreeBoardController2 {

	@Autowired
	private FreeBoardService2 service;

	@Autowired
	private FreeBoardService FreeBoardService;

	// ========== 모든 메서드 실행 전에 자동으로 실행됨 ==========
	@ModelAttribute
	public void ensureLoginMember(HttpSession session) {
		// 테스트용 로그인 회원 세션 생성 (세션이 없을 때만 생성)
		if (session.getAttribute("loginMember") == null) {
			Member fakeMember = new Member();
			fakeMember.setMemberNo(3);  // 실제 존재하는 회원 번호로 변경!
			fakeMember.setMemberNickname("유저일");
			session.setAttribute("loginMember", fakeMember);
		}
	}
	// ======================================================

	// 게시글 작성 화면 전환
	@GetMapping("/{boardCode:[0-9]+}/insert")
	public String FreeboardInsert(@PathVariable("boardCode") int boardCode) {
		return "board/freeboard/freeboardWrite";
	}

	// 게시글 작성
	@PostMapping("/{boardCode:[0-9]+}/insert")
	public String FreeboardInsert(
			@PathVariable("boardCode") int boardCode,
			Board board,
			@RequestParam(value="images", required=false) List<MultipartFile> images,
			HttpSession session,
			RedirectAttributes ra,
			HttpServletRequest req  
	) throws IllegalStateException, IOException {
		
		// 세션에서 loginMember 가져오기
		Member loginMember = (Member) session.getAttribute("loginMember");
		System.out.println("로그인한 회원 번호 : " + loginMember.getMemberNo());
		
		// 1. 로그인한 회원번호와 boardCode를 board에 세팅
		board.setBoardCode(boardCode);
		board.setMemberNo(loginMember.getMemberNo());
		
		
		String folderPath = req.getSession().getServletContext().getRealPath("/images/board/freeboard/");
		File folder = new File(folderPath);
		if (!folder.exists()) folder.mkdirs(); // 폴더 자동 생성

		System.out.println("이미지 실제 저장 경로 : " + folderPath);

	    
		// 2. 게시글 삽입 서비스 호출 후 게시글 번호 반환 받기
		int boardNo = service.FreeboardInsert(board, images);
		
		String message = null;
		String path = "redirect:";
		if(boardNo > 0) {
			path += "/board/" + boardCode + "/" + boardNo;
			message = "게시글이 등록되었습니다.";
		} else {
			path += "insert";
			message = "게시글 등록 실패 ㅠㅠ";
		}
		
		ra.addFlashAttribute("message", message);
		return path;
	}
	
	// 게시글 수정 화면 전환
	@GetMapping("/{boardCode}/{boardNo}/update")
	public String FreeboardUpdate(
			@PathVariable("boardCode") int boardCode,
			@PathVariable("boardNo") int boardNo,
			Model model
	) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);
		
		Board board = FreeBoardService.selectFreeBoardDetail(map);
		model.addAttribute("board", board);
		return "board/freeboard/freeboardUpdate";
	}
	
	// 게시글 수정
	@PostMapping("/{boardCode}/{boardNo}/update")
	public String FreeboardUpdate(
			@PathVariable("boardCode") int boardCode,
			@PathVariable("boardNo") int boardNo,
			Board board,
			@RequestParam(value="cp", required=false, defaultValue="1") String cp,
			@RequestParam(value="deleteList", required=false) String deleteList,
			@RequestParam(value="images", required=false) List<MultipartFile> images,
			RedirectAttributes ra
	) throws IllegalStateException, IOException {
		
		board.setBoardNo(boardNo);
		
		int rowCount = service.FreeboardUpdate(board, images, deleteList);
		String message = null;
		String path = "redirect:";
		
		if(rowCount > 0) {
			message = "게시글이 수정되었습니다.";
			path += "/board/" + boardCode + "/" + boardNo + "?cp=" + cp;
		} else {
			message = "게시글 수정 실패 ㅠㅠ";
			path += "update";
		}
		
		ra.addFlashAttribute("message", message);
		return path;
	}
	
	// 게시글 삭제
	@GetMapping("/{boardCode}/{boardNo}/delete")
	public String boardDelete(
			@PathVariable("boardCode") int boardCode,
			@PathVariable("boardNo") int boardNo,
			@RequestParam(value="cp", required=false, defaultValue="1") String cp,
			RedirectAttributes ra,
			@RequestHeader("referer") String referer
	) {
		int result = service.FreeboardDelete(boardNo);
		
		String message = null;
		String path = "redirect:";
		
		if(result > 0) {
			message = "게시글이 삭제되었습니다.";
			path += "/board/" + boardCode;
		} else {
			path += referer;
		}
		
		ra.addFlashAttribute("message", message);
		return path;
	}
}