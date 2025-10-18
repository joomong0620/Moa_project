package edu.og.moa.board.exhibition.controller;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.og.moa.board.exhibition.model.dto.BoardDB;
import edu.og.moa.board.exhibition.model.dto.Exhibition;
import edu.og.moa.board.exhibition.model.dto.MemberDB;
import edu.og.moa.board.exhibition.model.service.ExhibitionService;
import edu.og.moa.board.exhibition.model.service.ExhibitionService2;
import edu.og.moa.member.model.dto.Member;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
@RequestMapping("/board2") 
public class ExhibitionController2 {

	@Autowired
	public ExhibitionService2 service;
	
	@Autowired 
	private ExhibitionService exhibitionService; // 조회 서비스
	
	// 게시글 작성 화면 전환
	@GetMapping("/{communityCode:[3]+}/insert") 
	public String exhibitionInsert(@PathVariable("communityCode") int communityCode
			) {
		Map<String, Object> map = new HashMap<String, Object>(); 
		log.info("Exhibition detail communityCode: {}", communityCode); 	
		map.put("communityCode", communityCode);	
		
		return "board/exhibition/exhibitionWrite";  
	}
	
	
	// 게시글 작성
	@PostMapping("/{communityCode:[3]+}/insert")
	public String boardInsert(@PathVariable("communityCode") int communityCode
			, Exhibition exhibition 
			, @RequestParam(value="images", required=false) List<MultipartFile> images 
			, HttpSession session 
			, @SessionAttribute("loginMember") Member loginMember 
			, RedirectAttributes ra
			) throws IllegalStateException, IOException {


		
		// 1. POST dao작업을 위해 우선,  어느회원이 어떤 게시글에 insert하는지 알아야 함 
		//   로그인한 회원번호와 boardCode를 board에 세팅
		exhibition.setCommunityCode(communityCode);  
		exhibition.setMemberNo(loginMember.getMemberNo()); 
			
		// 2. 게시글 삽입 서비스 호출 후 게시글 번호 반환 받기
		int exhibitNo = service.exhibtionInsert(exhibition, images);
		
		// 4. 게시글 삽입 서비스 호출 결과 후처리
		// 게시글 삽입 성공시  -> 방금 삽입한 게시글의 상세 조회 페이지로 리다이렉트
		String message = null;
		String path = "redirect:";
		if (exhibitNo > 0) {
			path += "/board/" + communityCode + "/" + exhibitNo; 
			message = "게시글이 등록 되었습니다.";			
		} else {
			path += "insert";
			message = "게시글 등록 실패 ^__^";
		}
		
		ra.addFlashAttribute("message", message); 

		return path;
	}
	
	
	@GetMapping("/{communityCode}/{exhibitNo}/update") 
	public String boardUpdate(@PathVariable("communityCode") int communityCode 
			, @PathVariable("exhibitNo") int exhibitNo 
			, Model model 
			) throws IllegalStateException, IOException {
		
		
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("communityCode", communityCode);
		map.put("boardNo", exhibitNo); 
		Exhibition exhibition = exhibitionService.selectExhibition(map); 
												 
		log.info("map for .selectExhibition(map):{}", map);
		log.info("exhibition in boardUpdate-GET:{}", exhibition); 

		model.addAttribute("exhibition", exhibition); 
		
		return "board/exhibition/exhibitionUpdate"; 
	}
	
	
	// 게시글 수정 (POST작업)
	@PostMapping("/{communityCode}/{exhibitNo}/update")  
	public String boardUpdate(
			@PathVariable("communityCode") int communityCode,
			@PathVariable("exhibitNo") int exhibitNo,
			Exhibition exhibition, 
			@RequestParam(value="cp", required=false, defaultValue="1") String cp, 
			@RequestParam(value="deleteList", required=false) String deleteList, 
			@RequestParam(value="images", required=false) List<MultipartFile> images, 
			RedirectAttributes ra, 
			HttpSession session 
			) throws IllegalStateException, IOException {
		
		
		// 1. boardNo를 커맨드 객체에 세팅
		exhibition.setExhibitNo(exhibitNo);  
		
		int rowCount = service.exhibitionUpdate(exhibition, images, deleteList); // BOARD, BOARD_IMG, EXHIBITION, 등 업데이트해야함 
		
		// 2. message, path 설정
		String message = null;
		String path = "redirect:";
		
		if(rowCount > 0) { // 게시글 수정 성공 시
			message = "게시글이 수정되었습니다";
			path += "/board/" + communityCode + "/" + exhibitNo + "?cp=" + cp; 
			
		} else { // 실패 시
			message = "게시글 수정 실패 ^___^";
			path += "update"; 
			
		}
		
		ra.addFlashAttribute("message", message);
		
		return path;
	}
	
	
	@GetMapping("/{communityCode}/{exhibitNo}/delete") 
	public String boardDelete(@PathVariable("communityCode") int communityCode 
			, @PathVariable("exhibitNo") int exhibitNo 
			, @RequestParam(value="cp", required=false, defaultValue="1") String cp
			, RedirectAttributes ra 
			, @RequestHeader("referer") String referer // 이전 요청 주소
			) {
		
		
		// 1. 게시글 삭제 서비스 호출
		int result = service.exhibitionDelete(exhibitNo);
		
		// 2. 결과에 따라 message, path 설정
		String message = null;
		String path = "redirect:";
		
		if (result > 0) {
			
			// - 게시글 삭제 성공 시 :"게시글이 삭제되었습니다." + 해당 게시판 목록 첫페이지
			message = "게시글이 삭제되었습니다.";
			path += "/board/" + communityCode;
		} else {
			// - 게시글 삭제 실패 시 :"게시글 삭제 실패 ^___^" + 해당 게시글 상세 화면
			message = "게시글 삭제 실패 ^___^";

			path += referer; // 마찬가지
				
		}
		
		ra.addFlashAttribute("message", message);
		
		//return null;		
		return path;
		
	}
	
}

