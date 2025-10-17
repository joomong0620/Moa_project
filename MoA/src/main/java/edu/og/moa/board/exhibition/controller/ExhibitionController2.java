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
@RequestMapping("/board2")  // "/board2/3/insert"
public class ExhibitionController2 {

	@Autowired
	public ExhibitionService2 service;
	
	@Autowired // 게시글 수정시 수정하는 게시글 상세조회 서비스 호출용
	private ExhibitionService exhibitionService; // 조회 서비스
	
	// 게시글 작성 화면 전환
	@GetMapping("/{communityCode:[3]+}/insert") // from JS: location.href = '주소' = /board2/3/insert (GET 방식) 
	public String exhibitionInsert(@PathVariable("communityCode") int communityCode
			) {
		// @PathVariable : 주소 값 가져오기 + request scope에 값 올리기
	
		Map<String, Object> map = new HashMap<String, Object>(); 
		log.info("Exhibition detail communityCode: {}", communityCode); // ok	
		map.put("communityCode", communityCode);	// communityCode값 같이 보낸다
		
		return "board/exhibition/exhibitionWrite";  // forward하겠다. (리다이렉트는 "redirect:board/boardWrite"로 해야함)
	}
	
	
	// 게시글 작성
	@PostMapping("/{communityCode:[3]+}/insert")
	public String boardInsert(@PathVariable("communityCode") int communityCode
			, /*@ModelAttribute*/ Exhibition exhibition // 커맨드 객체 (필드에 전달받은 파라미터 값 담겨있음)
			, @RequestParam(value="images", required=false) List<MultipartFile> images // Board.java에서는 private List<BoardImage> imageList로 리스트 타입않맞아 담을수 없음
						// 이미지는 필수가 아니므로  required=false이다
			, HttpSession session // 파일 저장 경로
			//, @SessionAttribute("loginMember") Member loginMember // 누가 썻는지 알아야 한다
			, @SessionAttribute("loginMember") MemberDB loginMember // 누가 썻는지 알아야 한다
			, RedirectAttributes ra
			// 파라미터 : 제목, 내용, 파일(0~5개)
			// 파일 저장 경로 : HttpSession
			// 세션 : 로그인한 회원의 번호
			// 리다이렉트 시 데이터 전달 : RedirectAttributes
			// 작성 성공 시 이동할 게시판 코드:@PathVariable("communityCode"), PathVariable데 담겨있는 보드코드 사용
			) throws IllegalStateException, IOException {
		/* List<MultipartFile>
		 * - 업로드된 이미지가 없어도 List에 MultipartFile 객체가 추가됨(size=0으로)
		 * 
		 * - 단, 업로드된 이미지가 없는 MultipartFile 객체는
		 * 	 파일크기(size)가 0 또는 파일명(getOriginalFileName())이 ""
		 * 
		 */

		
		// 1. POST dao작업을 위해 우선,  어느회원이 어떤 게시글에 insert하는지 알아야 함 
		//   로그인한 회원번호와 boardCode를 board에 세팅
		exhibition.setCommunityCode(communityCode);  // 얘들은 sql 쿼리를 위해 여기서 직접 exhibition에 세팅
		exhibition.setMemberNo(loginMember.getMemberNo()); // 얘들은 sql 쿼리를 위해 여기서 직접 exhibition에 세팅
		
		// 2. 이제 보낼 이미지: 업로드된 이미지 서버에 실제로 저장되는 경로와
		//    웹에서 요청 시 이미지를 볼 수 있는 경로 (웹 접근 경로)// 
		////String webPath = "/resources/images/board/exhibition";
		////String webPath = "/resources/images/board/exhibition/";
		////String webPath = "/images/board/exhibition/";
		//String webPath = "classpath:/static/images/board/exhibition/";	
		//String filePath = session.getServletContext().getRealPath(webPath); // getServletContext(): application scope 세팅되어 있음
		//System.out.println("RealPath of webPath:" + filePath); // /tmp/tomcat-docbase.8088.6305672398411714599/classpath:/static/images/board/exhibition/
		
		// 2. 게시글 삽입 서비스 호출 후 게시글 번호 반환 받기
		//int boardNo = service.boardInsert(board, images, webPath, filePath);
		//int exhibitNo = service.exhibtionInsert(exhibition, images, webPath, filePath);
		int exhibitNo = service.exhibtionInsert(exhibition, images);
//		int boardNo = service.boardInsert(board, images); 	// insert 할것은 board -> BOARD DB, images -> BOARD_IMG DB 
															// boardWrite.html에서 넘어올때도 Board board 와  List<MultipartFile> images 로 각각 넘어왔다
															// boardWrite.html에 있는 form-tag enctype="multipart/form-data" 에 의해 
															//  1) input type="text"에 담긴 제목(문자열)과 textarea에 담긴 내용(문자열)을  DTO(Board)로 바꿔 controller로 전달 -> @ModelAttribute Board board 
															// 2) input type="file"에 담긴 이미지(파일)을 MultiPartFile로 controller로 전달 -> @RequestParam(value="images", required=false) List<MultipartFile> images

		
		// 4. 게시글 삽입 서비스 호출 결과 후처리
		// 게시글 삽입 성공시 
		// -> 방금 삽입한 게시글의 상세 조회 페이지로 리다이렉트
		// -> /board/1/1501
		String message = null;
		String path = "redirect:";
		if (exhibitNo > 0) {
			//path = "board/boardDetail"; // boardDetail 페이지로 forward
			path += "/board/" + communityCode + "/" + exhibitNo; // "redirect:" 가 prefix로 있어야 redirect 된다.
			message = "게시글이 등록 되었습니다.";
			//model.addAttribute("boardNo", boardNo);			
			//ra.addFlashAttribute("message", message)			
			
			
		} else {
			// 게시글 삽입 실패 시 
			// -> 게시글 작성 페이지로 리다이렉트
			path += "insert";
			message = "게시글 등록 실패 ^__^";
		}
		
		ra.addFlashAttribute("message", message); // alert메시지 출력
		//return null;
		return path;
	}
	
	
	// 게시글 수정 화면 전환 (GET방식 요청: ==> "?cp=1" 같은 쿼리 스트링이 붙지만, 이게 요청주소로 포함되지는 않는다.)
	// http://localhost:8088/board2/3/927/update?cp=1
	@GetMapping("/{communityCode}/{exhibitNo}/update") // /board2/1/1500/update 부분   // ?cp=1 
	public String boardUpdate(@PathVariable("communityCode") int communityCode 
			, @PathVariable("exhibitNo") int exhibitNo 
			, Model model // forward에서 값 전달해 주는 데이터 전달용 객체, request scope
			) throws IllegalStateException, IOException {
		
		
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("communityCode", communityCode);
		//map.put("exhibitNo", exhibitNo);
		map.put("boardNo", exhibitNo); // exhibitNo === boardNo, 이고 .selectExhibition(map)실행 mapper.xml에서는 boardNo을 기다리고있다.
		
		// 게시글 상세 조회 서비스 호출 (<- 게시글 수정을 위해 현재 데이터 조회해 오기)
		// 수정할 데이터를 세션에 담아놓고 써도 되나, DB에서 게시글 상세조회로 수정할 데이터를 얻어 온다.
		Exhibition exhibition = exhibitionService.selectExhibition(map); // 조회 서비스
												 
		log.info("map for .selectExhibition(map):{}", map);
		log.info("exhibition in boardUpdate-GET:{}", exhibition); // null 넘어온다.. why?

		model.addAttribute("exhibition", exhibition); // exhibitionUpdate.html에 exhibition에서 값 꺼내서 화면 보여주기 구성한다.
		
		//return null;
		return "board/exhibition/exhibitionUpdate"; // 앞에 "redirect:"없으면 모두 forward
	}
	
	
	// 게시글 수정 (POST작업)
	@PostMapping("/{communityCode}/{exhibitNo}/update")  // GET과 주소같다
	// http://localhost:8088/board2/3/927/update?cp=1
	public String boardUpdate(
			@PathVariable("communityCode") int communityCode,
			@PathVariable("exhibitNo") int exhibitNo,
			Exhibition exhibition, // 커맨드 객체(조건: name == 필드 인 경우 필드에 파라미터(form-tag 입력 파라미터) 세팅)
			@RequestParam(value="cp", required=false, defaultValue="1") String cp, // 쿼리스트링 cp (input type="hidden"에 숨겨놓은값)
			@RequestParam(value="deleteList", required=false) String deleteList, // 삭제할 이미지 순서 deleteSet값 (input type="hidden"에 숨겨놓은값)
			@RequestParam(value="images", required=false) List<MultipartFile> images, //  업로드된 파일 리스트 (아무것도 안올려도 5개, 실제 올린건지는 size=0로 판단)
			RedirectAttributes ra, //리다이렉트시 값 전달용
			HttpSession session // 서버 파일 저장 경로를 얻어올 용도
			) throws IllegalStateException, IOException {
		
		
		// 1. boardNo를 커맨드 객체에 세팅
		exhibition.setExhibitNo(exhibitNo);  // exhibitNo === BoardDB.boardNo;
		
//		// 2. 이미지 서버 저장 경로, 웹 접근 경로(webapp기준)
//		String webPath = "/resources/images/board/";
//		String filePath = session.getServletContext().getRealPath(webPath);
//		
//		// 3. 게시글 수정 서비스 호출 (제목/내용수정:BOARD + 이미지수정:BOARD_IMG)
//		int rowCount = service.boardUpdate(board, images, webPath, filePath, deleteList); // BOARD, BOARD_IMG
		//int rowCount = service.boardUpdate(exhibition, images, deleteList); // BOARD, BOARD_IMG, EXHIBITION, 등 업데이트해야함 
		int rowCount = service.exhibitionUpdate(exhibition, images, deleteList); // BOARD, BOARD_IMG, EXHIBITION, 등 업데이트해야함 
		
		// 4. 결과에 따라 message, path 설정
		// - 수정 성공 시: 상세 조회 페이지 + "게시글이 수정되었습니다"
		// - 수정 실패 시: 수정 페이지 + "게시글 수정 실패 ^___^"
		
		String message = null;
		String path = "redirect:";
		
		if(rowCount > 0) { // 게시글 수정 성공 시
			message = "게시글이 수정되었습니다";
			path += "/board/" + communityCode + "/" + exhibitNo + "?cp=" + cp; //  boardCode, boardNo -> 
			
		} else { // 실패 시
			message = "게시글 수정 실패 ^___^";
			path += "update"; // 상대경로로
			
		}
		
		ra.addFlashAttribute("message", message);
		
		
		//return null;
		return path;
	}
	
	
	@GetMapping("/{communityCode}/{exhibitNo}/delete") // "/1/1500/delete"
	public String boardDelete(@PathVariable("communityCode") int communityCode 
			, @PathVariable("exhibitNo") int exhibitNo 
			, @RequestParam(value="cp", required=false, defaultValue="1") String cp
			//, @PathVariable(value="cp", required=false, defaultValue="1") String cp
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
			// 게시글
			message = "게시글이 삭제되었습니다.";
			path += "/board/" + communityCode;
		} else {
			// - 게시글 삭제 실패 시 :"게시글 삭제 실패 ^___^" + 해당 게시글 상세 화면
			// 게시글
			message = "게시글 삭제 실패 ^___^";
			//path += "/board/" + boardCode + "/" + boardNo + "?cp=" + cp;
			path += referer; // 마찬가지
				
		}
		
		ra.addFlashAttribute("message", message);
		
		//return null;		
		return path;
		
	}
	
}

