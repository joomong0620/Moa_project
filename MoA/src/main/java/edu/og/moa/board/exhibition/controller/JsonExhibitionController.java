package edu.og.moa.board.exhibition.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.og.moa.board.exhibition.model.dto.Author;
import edu.og.moa.board.exhibition.model.dto.Board;
import edu.og.moa.board.exhibition.model.dto.BoardImg;
import edu.og.moa.board.exhibition.model.dto.Contributor;
import edu.og.moa.board.exhibition.model.dto.Exhibition;
import edu.og.moa.board.exhibition.model.dto.JsonBoardImage;
import edu.og.moa.board.exhibition.model.dto.JsonExhibition;
import edu.og.moa.board.exhibition.model.dto.JsonPagination;
import edu.og.moa.board.exhibition.model.dto.JsonMember;
import edu.og.moa.board.exhibition.model.dto.Like;
import edu.og.moa.board.exhibition.model.service.JsonExhibitionService;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/board/exhibition")
@Controller
@Slf4j
@SessionAttributes("loginMember") 
public class JsonExhibitionController {

	@Autowired
	private JsonExhibitionService jsonExhibitionService;
	
	
	@GetMapping("/jsonExhibitionList")  
	public String jsonExhibitionList(Model model) throws IOException {

        String targetFileName = "mergeDbApiExhibition_mockSM230_20251006_125401.json"; 
        
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/board/exhibition/" + targetFileName);
        
        if (inputStream == null) {
            throw new IOException(targetFileName + " 파일을 찾을 수 없습니다.");
        }

        log.info("타겟 파일 이름 : {}", targetFileName);
        
        List<JsonExhibition> exhibitionDtoItems = new ArrayList<>();
        
        ObjectMapper mapper = new ObjectMapper();
        
        try {

        	Map<String,  List<JsonExhibition>> map = mapper.readValue(inputStream, 
        			new TypeReference<Map<String, List<JsonExhibition>>>() {}
        	); 
        	
        	// 데이터 접근
        	List<JsonExhibition> itemsList = map.get("itemsList"); 

        	Map<String, Object> mapExhibitionServiceImpl = new HashMap<>();
        	
        	int listCount = 209; 	// fixed 1 for mockSM230 json 데이터
        	int cp = 1; 			// fixed 1 for mockSM230 json 데이터
        	JsonPagination pagination = new JsonPagination(cp, listCount);
        	int paginationLimit = pagination.getLimit();
        	
        	// frontend로 전달: 
        	List<JsonExhibition> itemsListPageLimit10 = new ArrayList<>(itemsList.subList(0, paginationLimit));
        	
			mapExhibitionServiceImpl.put("exhibitionList", itemsListPageLimit10);
			
        	mapExhibitionServiceImpl.put("pagination", pagination);
        				
			// 조회 결과를 request scope에 세팅 후 forward
			model.addAttribute("map", mapExhibitionServiceImpl); //model : spring에서 사용하는 데이터 전달 객체 => js에서 이걸 받아 사용 (@PathVariable에 담긴 boardCode와 cp도 담겨져 넘어감)

			// 로그인 서비스 mock:		
			JsonMember loginMember = new JsonMember();
			loginMember.setMemberNickname("한국문화정보원");
			loginMember.setProfileImage("/images/board/exhibition/member/penguin.jpeg"); 
			
			loginMember.setMemberNo(3); // 임의할당 for testing (cf:  전시 exhibitionCode === boarcCode ===  communityCode = 3)
			
			model.addAttribute("loginMember", loginMember); // 
        	
        	// boardCode 값 mock으로 넘겨주기:
			model.addAttribute("exhibitionCode", 3);  //boardCode === exhibitionCode === communityCode
			model.addAttribute("exhibitionName", "전시게시판");  //boardName === exhibitionName === communityName
			
        } catch (Exception e) {
        	e.printStackTrace();
        }
	    
		return "board/exhibition/jsonExhibitionList";
	}
	


	@GetMapping("/jsonExhibitionDetail")  
	public String jsonExhibitionDetail(	Model model	) throws IOException {

        String targetFileName = "mergeDbApiExhibition_mockSM230_20251006_125401.json"; // targetFileName for API DTO -> Exhibition DTO mapping
        
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/board/exhibition/" + targetFileName);
        
        if (inputStream == null) {
            throw new IOException(targetFileName + " 파일을 찾을 수 없습니다.");
        }	

        log.info("타겟 파일 이름 : {}", targetFileName);
        
        List<JsonExhibition> exhibitionDtoItems = new ArrayList<>();
        
        ObjectMapper mapper = new ObjectMapper();
        
        try {
        	Map<String,  List<JsonExhibition>> map = mapper.readValue(inputStream, 
        			new TypeReference<Map<String, List<JsonExhibition>>>() {}
        	); 
        	
        	List<JsonExhibition> itemsList = map.get("itemsList");

			JsonExhibition exhibition = null;
			//int index = 0; // 0번째 게시글: exhibitTitle=관동팔경 Ⅱ, 양양 낙산사
			int index = 201; // 221번째 게시글: exhibitTitle=만세불후萬世不朽-돌에 새긴 영원
			if (index < 0 || index >= itemsList.size()) {
				throw new IndexOutOfBoundsException("유효하지 않은 인덱스입니다.");
			} else {
					exhibition = itemsList.get(index);
			}


        	// frontend로 전달
			model.addAttribute("exhibition", exhibition); 
			
			// 로그인 서비스 mock:		
			JsonMember loginMember = new JsonMember();
			loginMember.setMemberNickname("한국문화정보원");
			loginMember.setProfileImage("/images/board/exhibition/member/penguin.jpeg"); 
			loginMember.setMemberNo(3); 
			model.addAttribute("loginMember", loginMember); 
			
			// boardCode 값 mock
			model.addAttribute("exhibitionCode", 3);  
			model.addAttribute("exhibitionName", "전시게시판");  
			
 			JsonBoardImage thumbnail = null; 
 			if(!exhibition.getImageList().isEmpty()) {
				
 				// 썸네일 == 0번 인덱스 이미지의 순서가 0인 경우
 				if(exhibition.getImageList().get(0).getImageOrder() == 0) {
 					thumbnail = exhibition.getImageList().get(0); // exhibitionImage 객체
 				}
				
 				model.addAttribute("thumbnail", thumbnail); 
						
 			}
			
			model.addAttribute("start", thumbnail != null ? 1 : 0); // 삼항 연산자
			
			
        } catch (Exception e) {
        	e.printStackTrace();
        }
	    
		return "board/exhibition/jsonExhibitionDetail";
	}


	
	@GetMapping("/jsonExhibitionUpdate")  
	public String jsonExhibitionUpdate(	Model model	) throws IOException {

        String targetFileName = "mergeDbApiExhibition_mockSM230_20251006_125401.json"; // targetFileName for API DTO -> Exhibition DTO mapping
        
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/board/exhibition/" + targetFileName);
        
        if (inputStream == null) {
            throw new IOException(targetFileName + " 파일을 찾을 수 없습니다.");
        }	
        
        log.info("타겟 파일 이름 : {}", targetFileName);
        
        List<JsonExhibition> exhibitionDtoItems = new ArrayList<>();
        
        ObjectMapper mapper = new ObjectMapper();
        
        try {
        	
        	Map<String,  List<JsonExhibition>> map = mapper.readValue(inputStream, 
        			new TypeReference<Map<String, List<JsonExhibition>>>() {}
        	); 
        	
        	List<JsonExhibition> itemsList = map.get("itemsList"); 

			JsonExhibition exhibition = null;
			//int index = 0; // 0번째 게시글: exhibitTitle=관동팔경 Ⅱ, 양양 낙산사
			int index = 201; // 221번째 게시글: exhibitTitle=만세불후萬世不朽-돌에 새긴 영원
			if (index < 0 || index >= itemsList.size()) {
				throw new IndexOutOfBoundsException("유효하지 않은 인덱스입니다.");
			} else {
					exhibition = itemsList.get(index);
			}
			

        	// frontend로 전달
			model.addAttribute("exhibition", exhibition); 
			
			// 로그인 서비스 mock:		
			JsonMember loginMember = new JsonMember();
			loginMember.setMemberNickname("한국문화정보원");
			loginMember.setProfileImage("/images/board/exhibition/member/penguin.jpeg"); 			
			loginMember.setMemberNo(3); 
			model.addAttribute("loginMember", loginMember); 
			
        	// boardCode 값 mock
			model.addAttribute("exhibitionCode", 3);  
			model.addAttribute("exhibitionName", "전시게시판");  
			
 			// 게시글 이미지가 있는 경우
 			JsonBoardImage thumbnail = null; 
 			if(!exhibition.getImageList().isEmpty()) {
				
 				// 썸네일 == 0번 인덱스 이미지의 순서가 0인 경우
 				if(exhibition.getImageList().get(0).getImageOrder() == 0) {
 					thumbnail = exhibition.getImageList().get(0); 
 				}
				
 				model.addAttribute("thumbnail", thumbnail); 
						
 			}
			

			model.addAttribute("start", thumbnail != null ? 1 : 0); 
			
			
        } catch (Exception e) {
        	e.printStackTrace();
        }
	    
		return "board/exhibition/jsonExhibitionUpdate";
		//return "main";
	}
	
	
	
	
	@GetMapping("/jsonExhibitionWrite")  
	public String jsonExhibitionWrite( Model model ) throws IOException {

        String targetFileName = "mergeDbApiExhibition_mockSM230_20251006_125401.json"; // targetFileName for API DTO -> Exhibition DTO mapping
        
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/board/exhibition/" + targetFileName);
        
        if (inputStream == null) {
            throw new IOException(targetFileName + " 파일을 찾을 수 없습니다.");
        }	
        
        log.info("타겟 파일 이름 : {}", targetFileName);
        
        
        List<JsonExhibition> exhibitionDtoItems = new ArrayList<>();
        
        ObjectMapper mapper = new ObjectMapper();
        
        try {

        	Map<String,  List<JsonExhibition>> map = mapper.readValue(inputStream, 
        			new TypeReference<Map<String, List<JsonExhibition>>>() {}
        	); 
        	
        	List<JsonExhibition> itemsList = map.get("itemsList"); 

			JsonExhibition exhibition = null;
			//int index = 0; // 0번째 게시글: exhibitTitle=관동팔경 Ⅱ, 양양 낙산사
			int index = 201; // 221번째 게시글: exhibitTitle=만세불후萬世不朽-돌에 새긴 영원
			if (index < 0 || index >= itemsList.size()) {
				throw new IndexOutOfBoundsException("유효하지 않은 인덱스입니다.");
			} else {
					exhibition = itemsList.get(index);
			}

        	// frontend로 전달
			// 로그인 서비스 mock:		
			JsonMember loginMember = new JsonMember();
			loginMember.setMemberNickname("한국문화정보원");
			loginMember.setProfileImage("/images/board/exhibition/member/penguin.jpeg"); 
			loginMember.setMemberNo(3); 
			model.addAttribute("loginMember", loginMember); 
        	
        	// // boardCode 값 mock
			model.addAttribute("exhibitionCode", 3);  
			model.addAttribute("exhibitionName", "전시게시판");  
			

 			// 게시글 이미지가 있는 경우
 			JsonBoardImage thumbnail = null; 
 			if(!exhibition.getImageList().isEmpty()) {
				
 				// 썸네일 == 0번 인덱스 이미지의 순서가 0인 경우
 				if(exhibition.getImageList().get(0).getImageOrder() == 0) {
 					thumbnail = exhibition.getImageList().get(0); 
 				}
					
 			}
			
 			model.addAttribute("start", 0); // for temporary check
			
        } catch (Exception e) {
        	e.printStackTrace();
        }
	    
	    JsonBoardImage boardImage = new JsonBoardImage();
	    List<JsonBoardImage> imageList = new ArrayList<>();
	    imageList.add(boardImage);
	    model.addAttribute("imageList", imageList);
	    
		return "board/exhibition/jsonExhibitionWrite";
	}	
	
	
	@GetMapping("/jsonToDatabaseInsert")  
	public String jsonToDatabaseInsert( Model model ) throws IllegalStateException, IOException {

        String targetFileName = "mergeDbApiExhibition_mockSM230rev_20251009_012902.json"; // targetFileName for API DTO -> Exhibition DTO mapping
        
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/board/exhibition/" + targetFileName);
        
        if (inputStream == null) {
            throw new IOException(targetFileName + " 파일을 찾을 수 없습니다.");
        }	
        
        log.info("타겟 파일 이름 : {}", targetFileName);
        
        
        List<JsonExhibition> exhibitionDtoItems = new ArrayList<>();
        
        ObjectMapper mapper = new ObjectMapper();
        
        try {

        	Map<String,  List<JsonExhibition>> map = mapper.readValue(inputStream, 
        			new TypeReference<Map<String, List<JsonExhibition>>>() {}
        	); 
        	
        	List<JsonExhibition> itemsList = map.get("itemsList"); 

        	// 여기서 리스트 원소 한개씩 꺼내서, 전처리하고, 각 DTO에 값 assign해주자
        	// boardNo는 700 ~ 909번까지 쓴다. (전시당 포스터 한개이므로, BOARD_IMG의 imgNo도 700~909까지 사용, imgOrder=0) 
        	for (int idx=700; idx < itemsList.size(); idx++) {
        		JsonExhibition jsonExhibition = itemsList.get(idx);
        		
        		// json data insert할 테이블들: BOARD, BOARD_IMG, EXHIBITION, AUTHOR, LIKE, CONTRIBUTOR
        		// DTO에 맵핑할때(jsonExhibition 필드값 할당) 필요시 data pre-processing 
        		
        		///////////
        		// 1) Board DTO에 맵핑하고, BOARD DB에 insert 
        		// DTO 값 할당
        		Board board = new Board();
        		board.setBoardNo(idx);
        		board.setBoardTitle(jsonExhibition.getExhibitTitle());
        		board.setBCreateDate(jsonExhibition.getExhibitCreateDate()); // 날짜 형식 맞춰줄 필요 있음, 예시 "2025-06-17 00:10:02"
        		board.setBUpdateDate(jsonExhibition.getExhibitUpdateDate()); // 날짜 형식 맞춰줄 필요 있음, 예시 "2025-06-17 00:10:02"
        		board.setBoardCount(jsonExhibition.getReadCount());
        		//board.setBoardDelFl(jsonExhibition.get__ ); // 현재 jsonExhibition에 매핑하는 필드없음. 게시글삭제 기능시 고려해야 할 사항으로 보임
        		board.setMemberNo(jsonExhibition.getMemberNo());
        		board.setCommunityCode(jsonExhibition.getCommunityCode());
        		//board.setQCode(jsonExhibition.get__ ); // 현재 jsonExhibition에 매핑하는 필드없음; DB nullalbe (default:1 , 또는 null입력하여 null처리가능)
        		
        		// DB 서비스 호출
        		//int boardNo = jsonExhibitionService.jsonBoardInsert(board); 	
        		int result = jsonExhibitionService.jsonBoardInsert(board); 
        		//int boardNo = jsonExhibitionService.jsonBoardInsert(board, images); 	// insert 할것은 board -> BOARD DB, images -> BOARD_IMG DB 
				// boardWrite.html에서 넘어올때도 Board board 와  List<MultipartFile> images 로 각각 넘어왔다
				// boardWrite.html에 있는 form-tag enctype="multipart/form-data" 에 의해 
				//  1) input type="text"에 담긴 제목(문자열)과 textarea에 담긴 내용(문자열)을  DTO(Board)로 바꿔 controller로 전달 -> @ModelAttribute Board board 
				// 2) input type="file"에 담긴 이미지(파일)을 MultiPartFile로 controller로 전달 -> @RequestParam(value="images", required=false) List<MultipartFile> images


       		
        		
        		
        		
        		
//        		///////////        		
//        		// 2) BoardImg DTO에 맵핑하고, BOARD_IMG DB에 insert 
//        		// DTO 값 할당
//        		BoardImg boardImg = new BoardImg();
//        		boardImg.setImgNo(idx);
//        		boardImg.setImgPath( );
//        		boardImg.setImgOrig( );
//        		boardImg.setImgRename( );
//        		boardImg.setImgOrder( );
//        		boardImg.setBoardNo(idx);       		
//        		// DB 서비스 호출
//
//        		///////////        		
//        		// 3) Exhibition DTO에 맵핑하고, EXHIBITION DB에 insert 
//        		// DTO 값 할당
//        		Exhibition exhibition = new Exhibition();
//        		exhibition.setBoardNo(idx);
//        		exhibition.setExhibitSubTitle( );
//        		exhibition.setExhibitDate( );
//        		exhibition.setExhibitContact( );
//        		exhibition.setExhibitAudience( );
//        		exhibition.setExhibitCharge( );
//        		exhibition.setInstitutionNo( );
//        		exhibition.setGenreNo( );
//        		// DB 서비스 호출
//        		
//        		///////////
//        		// 4) Author DTO에 맵핑하고, AUTHOR DB에 insert 
//        		// DTO 값 할당
//        		Author author = new Author(); 
//        		author.setAuthorNo( );
//        		author.setAuthorName( );
//        		author.setBoardNo(idx);
//        		// DB 서비스 호출       		
//        		
//        		
//        		///////////        		
//        		// 5) Like DTO에 맵핑하고, LIKE DB에 insert 
//        		// DTO 값 할당
//        		Like like = new Like(); 
//        		like.setBoardNo(idx);
//        		like.setMemberNo( ); // 조회수에 따라 멤버 넘버 랜덤하게 뽑아서 insert문을 그 수만큼 반복
//        		// DB 서비스 호출        		
//        		
//        		
//        		///////////        		
//        		// 6) Contributor DTO에 맵핑하고, CONTRIBUTOR DB에 insert 
//        		// DTO 값 할당
//        		Contributor contributor = new Contributor(); 
//        		contributor.setBoardNo(idx);
//        		contributor.setContributorNo(idx);
//        		contributor.setExhibitHost( );
//        		contributor.setExhibitSupport( );
//        		
//        		// DB 서비스 호출        		
        		
        		
        		///////////////////////////////////////////////////////
				// 게시글 삽입 서비스 호출 결과 후처리
				// 게시글 삽입 성공시 
				// -> 방금 삽입한 게시글의 상세 조회 페이지로 리다이렉트 (not-yet)
				// -> /board/1/1501
				String message = null;
				String path = "redirect:";
				if (result > 0) {
//				if (boardNo > 0) {
					////path = "board/boardDetail"; // boardDetail 페이지로 forward
					//path += "/board/" + boardCode + "/" + boardNo; // "redirect:" 가 prefix로 있어야 redirect 된다.
					path = "board/exhibition/jsonExhibitionList"; // boardDetail 페이지로 forward
					message = board.getBoardNo() + " 번째 게시글이 DB에 삽입 되었습니다.";
					log.info(message);
					//model.addAttribute("boardNo", boardNo);			
					//ra.addFlashAttribute("message", message)			

				} else {
					// 게시글 삽입 실패 시 
					// -> 게시글 작성 페이지로 리다이렉트
					//path += "insert";
					message = board.getBoardNo() + " 번째 게시글 DB이 실패했습니다. @@@@@";
					log.info(message);
					
					continue; // 다음 데이터 입력
				}
				
				//ra.addFlashAttribute("message", message); // alert메시지 출력
				////return null;
				//return path;         		
        		
        		break; // for testing
        		
        	}
        	
        	
        	
//			JsonExhibition exhibition = null;
//			//int index = 0; // 0번째 게시글: exhibitTitle=관동팔경 Ⅱ, 양양 낙산사
//			int index = 201; // 221번째 게시글: exhibitTitle=만세불후萬世不朽-돌에 새긴 영원
//			if (index < 0 || index >= itemsList.size()) {
//				throw new IndexOutOfBoundsException("유효하지 않은 인덱스입니다.");
//			} else {
//					exhibition = itemsList.get(index);
//			}
//
//        	// frontend로 전달
//			// 로그인 서비스 mock:		
//			JsonMember loginMember = new JsonMember();
//			loginMember.setMemberNickname("한국문화정보원");
//			loginMember.setProfileImage("/images/board/exhibition/member/penguin.jpeg"); 
//			loginMember.setMemberNo(3); 
//			model.addAttribute("loginMember", loginMember); 
//        	
//        	// // boardCode 값 mock
//			model.addAttribute("exhibitionCode", 3);  
//			model.addAttribute("exhibitionName", "전시게시판");  
//			
//
// 			// 게시글 이미지가 있는 경우
// 			JsonBoardImage thumbnail = null; 
// 			if(!exhibition.getImageList().isEmpty()) {
//				
// 				// 썸네일 == 0번 인덱스 이미지의 순서가 0인 경우
// 				if(exhibition.getImageList().get(0).getImageOrder() == 0) {
// 					thumbnail = exhibition.getImageList().get(0); 
// 				}
//					
// 			}
//			
// 			model.addAttribute("start", 0); // for temporary check
			
        } catch (Exception e) {
        	e.printStackTrace();
        }
	    
//        // empty imageList for testing
//	    JsonBoardImage boardImage = new JsonBoardImage();
//	    List<JsonBoardImage> imageList = new ArrayList<>();
//	    imageList.add(boardImage);
//	    model.addAttribute("imageList", imageList);
	    
		//return "board/exhibition/jsonToDatabaseInsert";
		//return null;
        return "board/exhibition/jsonExhibitionList";
        
	}	
	
}
