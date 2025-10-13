//package edu.og.moa.board.exhibition.controller;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.SessionAttributes;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import edu.og.moa.board.exhibition.model.dto.JsonBoardImage;
//import edu.og.moa.board.exhibition.model.dto.JsonExhibition;
//import edu.og.moa.board.exhibition.model.dto.JsonPagination;
//import edu.og.moa.board.exhibition.model.dto.JsonMember;
//import lombok.extern.slf4j.Slf4j;
//
//@RequestMapping("/board/exhibition")
//@Controller
//@Slf4j
//@SessionAttributes("loginMember") 
//public class ExhibitionControllerOld {
//
//	
//	@GetMapping("/jsonExhibitionList")  
//	public String jsonExhibitionList(Model model) throws IOException {
//
//        String targetFileName = "mergeDbApiExhibition_mockSM230_20251006_125401.json"; 
//        
//        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/board/exhibition/" + targetFileName);
//        
//        if (inputStream == null) {
//            throw new IOException(targetFileName + " 파일을 찾을 수 없습니다.");
//        }
//
//        log.info("타겟 파일 이름 : {}", targetFileName);
//        
//        List<JsonExhibition> exhibitionDtoItems = new ArrayList<>();
//        
//        ObjectMapper mapper = new ObjectMapper();
//        
//        try {
//
//        	Map<String,  List<JsonExhibition>> map = mapper.readValue(inputStream, 
//        			new TypeReference<Map<String, List<JsonExhibition>>>() {}
//        	); 
//        	
//        	// 데이터 접근
//        	List<JsonExhibition> itemsList = map.get("itemsList"); 
//
//        	Map<String, Object> mapExhibitionServiceImpl = new HashMap<>();
//        	
//        	int listCount = 209; 	// fixed 1 for mockSM230 json 데이터
//        	int cp = 1; 			// fixed 1 for mockSM230 json 데이터
//        	JsonPagination pagination = new JsonPagination(cp, listCount);
//        	int paginationLimit = pagination.getLimit();
//        	
//        	// frontend로 전달: 
//        	List<JsonExhibition> itemsListPageLimit10 = new ArrayList<>(itemsList.subList(0, paginationLimit));
//        	
//			mapExhibitionServiceImpl.put("exhibitionList", itemsListPageLimit10);
//			
//        	mapExhibitionServiceImpl.put("pagination", pagination);
//        				
//			// 조회 결과를 request scope에 세팅 후 forward
//			model.addAttribute("map", mapExhibitionServiceImpl); //model : spring에서 사용하는 데이터 전달 객체 => js에서 이걸 받아 사용 (@PathVariable에 담긴 boardCode와 cp도 담겨져 넘어감)
//
//			// 로그인 서비스 mock:		
//			JsonMember loginMember = new JsonMember();
//			loginMember.setMemberNickname("한국문화정보원");
//			loginMember.setProfileImage("/images/board/exhibition/member/penguin.jpeg"); 
//			
//			loginMember.setMemberNo(3); // 임의할당 for testing (cf:  전시 exhibitionCode === boarcCode ===  communityCode = 3)
//			
//			model.addAttribute("loginMember", loginMember); // 
//        	
//        	// boardCode 값 mock으로 넘겨주기:
//			model.addAttribute("exhibitionCode", 3);  //boardCode === exhibitionCode === communityCode
//			model.addAttribute("exhibitionName", "전시게시판");  //boardName === exhibitionName === communityName
//			
//        } catch (Exception e) {
//        	e.printStackTrace();
//        }
//	    
//		return "board/exhibition/exhibitionList";
//	}
//	
//
//
//	@GetMapping("/jsonExhibitionDetail")  
//	public String jsonExhibitionDetail(	Model model	) throws IOException {
//
//        String targetFileName = "mergeDbApiExhibition_mockSM230_20251006_125401.json"; // targetFileName for API DTO -> Exhibition DTO mapping
//        
//        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/board/exhibition/" + targetFileName);
//        
//        if (inputStream == null) {
//            throw new IOException(targetFileName + " 파일을 찾을 수 없습니다.");
//        }	
//
//        log.info("타겟 파일 이름 : {}", targetFileName);
//        
//        List<JsonExhibition> exhibitionDtoItems = new ArrayList<>();
//        
//        ObjectMapper mapper = new ObjectMapper();
//        
//        try {
//        	Map<String,  List<JsonExhibition>> map = mapper.readValue(inputStream, 
//        			new TypeReference<Map<String, List<JsonExhibition>>>() {}
//        	); 
//        	
//        	List<JsonExhibition> itemsList = map.get("itemsList");
//
//			JsonExhibition exhibition = null;
//			//int index = 0; // 0번째 게시글: exhibitTitle=관동팔경 Ⅱ, 양양 낙산사
//			int index = 201; // 221번째 게시글: exhibitTitle=만세불후萬世不朽-돌에 새긴 영원
//			if (index < 0 || index >= itemsList.size()) {
//				throw new IndexOutOfBoundsException("유효하지 않은 인덱스입니다.");
//			} else {
//					exhibition = itemsList.get(index);
//			}
//
//
//        	// frontend로 전달
//			model.addAttribute("exhibition", exhibition); 
//			
//			// 로그인 서비스 mock:		
//			JsonMember loginMember = new JsonMember();
//			loginMember.setMemberNickname("한국문화정보원");
//			loginMember.setProfileImage("/images/board/exhibition/member/penguin.jpeg"); 
//			loginMember.setMemberNo(3); 
//			model.addAttribute("loginMember", loginMember); 
//			
//			// boardCode 값 mock
//			model.addAttribute("exhibitionCode", 3);  
//			model.addAttribute("exhibitionName", "전시게시판");  
//			
// 			JsonBoardImage thumbnail = null; 
// 			if(!exhibition.getImageList().isEmpty()) {
//				
// 				// 썸네일 == 0번 인덱스 이미지의 순서가 0인 경우
// 				if(exhibition.getImageList().get(0).getImageOrder() == 0) {
// 					thumbnail = exhibition.getImageList().get(0); // exhibitionImage 객체
// 				}
//				
// 				model.addAttribute("thumbnail", thumbnail); 
//						
// 			}
//			
//			model.addAttribute("start", thumbnail != null ? 1 : 0); // 삼항 연산자
//			
//			
//        } catch (Exception e) {
//        	e.printStackTrace();
//        }
//	    
//		return "board/exhibition/exhibitionDetail";
//	}
//
//
//	
//	@GetMapping("/jsonExhibitionUpdate")  
//	public String jsonExhibitionUpdate(	Model model	) throws IOException {
//
//        String targetFileName = "mergeDbApiExhibition_mockSM230_20251006_125401.json"; // targetFileName for API DTO -> Exhibition DTO mapping
//        
//        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/board/exhibition/" + targetFileName);
//        
//        if (inputStream == null) {
//            throw new IOException(targetFileName + " 파일을 찾을 수 없습니다.");
//        }	
//        
//        log.info("타겟 파일 이름 : {}", targetFileName);
//        
//        List<JsonExhibition> exhibitionDtoItems = new ArrayList<>();
//        
//        ObjectMapper mapper = new ObjectMapper();
//        
//        try {
//        	
//        	Map<String,  List<JsonExhibition>> map = mapper.readValue(inputStream, 
//        			new TypeReference<Map<String, List<JsonExhibition>>>() {}
//        	); 
//        	
//        	List<JsonExhibition> itemsList = map.get("itemsList"); 
//
//        	JsonExhibition exhibition = null;
//			//int index = 0; // 0번째 게시글: exhibitTitle=관동팔경 Ⅱ, 양양 낙산사
//			int index = 201; // 221번째 게시글: exhibitTitle=만세불후萬世不朽-돌에 새긴 영원
//			if (index < 0 || index >= itemsList.size()) {
//				throw new IndexOutOfBoundsException("유효하지 않은 인덱스입니다.");
//			} else {
//					exhibition = itemsList.get(index);
//			}
//			
//
//        	// frontend로 전달
//			model.addAttribute("exhibition", exhibition); 
//			
//			// 로그인 서비스 mock:		
//			JsonMember loginMember = new JsonMember();
//			loginMember.setMemberNickname("한국문화정보원");
//			loginMember.setProfileImage("/images/board/exhibition/member/penguin.jpeg"); 			
//			loginMember.setMemberNo(3); 
//			model.addAttribute("loginMember", loginMember); 
//			
//        	// boardCode 값 mock
//			model.addAttribute("exhibitionCode", 3);  
//			model.addAttribute("exhibitionName", "전시게시판");  
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
// 				model.addAttribute("thumbnail", thumbnail); 
//						
// 			}
//			
//
//			model.addAttribute("start", thumbnail != null ? 1 : 0); 
//			
//			
//        } catch (Exception e) {
//        	e.printStackTrace();
//        }
//	    
//		return "board/exhibition/exhibitionUpdate";
//		//return "main";
//	}
//	
//	
//	
//	
//	@GetMapping("/jsonExhibitionWrite")  
//	public String jsonExhibitionWrite( Model model ) throws IOException {
//
//        String targetFileName = "mergeDbApiExhibition_mockSM230_20251006_125401.json"; // targetFileName for API DTO -> Exhibition DTO mapping
//        
//        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/board/exhibition/" + targetFileName);
//        
//        if (inputStream == null) {
//            throw new IOException(targetFileName + " 파일을 찾을 수 없습니다.");
//        }	
//        
//        log.info("타겟 파일 이름 : {}", targetFileName);
//        
//        
//        List<JsonExhibition> exhibitionDtoItems = new ArrayList<>();
//        
//        ObjectMapper mapper = new ObjectMapper();
//        
//        try {
//
//        	Map<String,  List<JsonExhibition>> map = mapper.readValue(inputStream, 
//        			new TypeReference<Map<String, List<JsonExhibition>>>() {}
//        	); 
//        	
//        	List<JsonExhibition> itemsList = map.get("itemsList"); 
//
//        	JsonExhibition exhibition = null;
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
//			
//        } catch (Exception e) {
//        	e.printStackTrace();
//        }
//	    
//	    JsonBoardImage boardImage = new JsonBoardImage();
//	    List<JsonBoardImage> imageList = new ArrayList<>();
//	    imageList.add(boardImage);
//	    model.addAttribute("imageList", imageList);
//	    
//		return "board/exhibition/exhibitionWrite";
//	}	
//	
//}
