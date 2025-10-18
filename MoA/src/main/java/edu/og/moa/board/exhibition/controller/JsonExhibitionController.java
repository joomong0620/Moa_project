package edu.og.moa.board.exhibition.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.og.moa.board.exhibition.model.dto.AuthorDB;
import edu.og.moa.board.exhibition.model.dto.BoardDB;
import edu.og.moa.board.exhibition.model.dto.BoardImgDB;
import edu.og.moa.board.exhibition.model.dto.ContributorDB;
import edu.og.moa.board.exhibition.model.dto.ExhibitionDB;
import edu.og.moa.board.exhibition.model.dto.JsonBoardImage;
import edu.og.moa.board.exhibition.model.dto.JsonExhibition;
import edu.og.moa.board.exhibition.model.dto.PaginationDB;
import edu.og.moa.board.exhibition.model.dto.JsonMember;
import edu.og.moa.board.exhibition.model.dto.LikeDB;
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
        	PaginationDB pagination = new PaginationDB(cp, listCount);
        	int paginationLimit = pagination.getLimit();
        	
        	// frontend로 전달: 
        	List<JsonExhibition> itemsListPageLimit10 = new ArrayList<>(itemsList.subList(0, paginationLimit));
        	
			mapExhibitionServiceImpl.put("exhibitionList", itemsListPageLimit10);
			
        	mapExhibitionServiceImpl.put("pagination", pagination);
        				
			// 조회 결과를 request scope에 세팅 후 forward
			model.addAttribute("map", mapExhibitionServiceImpl); 

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

        String targetFileName = "mergeDbApiExhibition_mockSM230rev_20251009_012902.json"; // targetFileName 
        
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

        	log.info("여기까지 OK 1");
        	// boardNo는 700 ~ 909번까지 쓴다. (전시당 포스터 한개이므로, BOARD_IMG의 imgNo도 700~909까지 사용, imgOrder=0) 
        	List<String> resultMessageList = new ArrayList<>(); 
        	for (int idx=0; idx < itemsList.size(); idx++) {
        		       		
        		//if (idx != 134) { continue; } // 특정글 테스트: exhibitNo =
        		
        		int idxOffset = idx+700; // 700번 부터
        		JsonExhibition jsonExhibition = itemsList.get(idx);
        		        		
        		///////////
        		// 1) Board DTO에 맵핑하고, BOARD DB에 insert 
        		// DTO 값 할당
        		BoardDB board = new BoardDB();
        		//board.setBoardNo(idxOffset); // boardNo은 generated Key로 얻자
        		//log.info("iOffset : {}", idxOffset);
        		log.info("idx : {}", idx);
        		board.setBoardTitle(jsonExhibition.getExhibitTitle());
        		
        		// ExhibitContent의 글자수 > 4000 이상인 경우 slicing 필요 (우리 칼럼은 VARCHAR2(4000 Byte) -> VARCHAR2(4000 CHAR)로 수정했음)
        		String lenExhibitContent = jsonExhibition.getExhibitContent();
        		String slicedExhibitContent = null;
        		if (lenExhibitContent != null) {
        			slicedExhibitContent = truncateByByte(lenExhibitContent, 4000 - " ...... ".getBytes(StandardCharsets.UTF_8).length);
        			slicedExhibitContent += " ......";
        		}        		
        		
        		
        		board.setBoardContent( slicedExhibitContent );
        		board.setBCreateDate(jsonExhibition.getExhibitCreateDate());// 날짜 형식 "2025-06-17 00:10:02"
        		board.setBUpdateDate(null);
        		
        		board.setBoardCount(jsonExhibition.getReadCount()); 		// random 생성 값 임의할당  
        		board.setMemberNo(jsonExhibition.getMemberNo()); 			// 10번, 한국문화정보원 으로 할당
        		board.setCommunityCode(jsonExhibition.getCommunityCode()); // 3번 전시게시판 값 할당
        		
        		// DB 서비스 호출
      			// ver1) idxOffset값(인덱스오프셋값)을 바로 boardNo로 입력
        		//int resultBoardDB = 0;
        		//resultBoardDB = jsonExhibitionService.jsonBoardInsert(board); 
        		
        		// ver2) SelectKey를 이용하여 가용한 시퀀스값 먼저 조회하여, 그것을 boardNo값으로 입력 (==> 나중에 사용자 insert할 때는 이걸로)
        		int boardNoBoardDB = jsonExhibitionService.jsonBoardInsertViaSelectKey(board); 
        		int resultBoardDB = boardNoBoardDB;
 
        		// 이제 아래에서 이 boardNo 사용
        		//int boardNo = idxOffset; // boardNo = idxOffset by API 입력, 또는 
        		int boardNo = board.getBoardNo(); //by 사용자
        		log.info("[BoardDB 데이터 삽입성공] boardNo generated by SEQ_BOARD_NO.NEXTVAL: {}", boardNo);
        		///////////        		
        		// 2) BoardImg DTO에 맵핑하고, BOARD_IMG DB에 insert 
        		// DTO 값 할당
        		String tmp_imageObject =  jsonExhibition.getExhibitImgObject(); 	//  split: 폴더경로(imagePath)와 파일이름(imageOriginal)
        		if (tmp_imageObject == null || tmp_imageObject.trim().isEmpty()){ 	// ImageObject 가 null일때는, 디폴트 monet_pond.jpg그림 경로담기
        			tmp_imageObject = "/images/board/exhibition/monet_pond.jpg";	// 웹 접근 경로
        		} // 이제 not-null	        		
        		
        		// # 예시) IMAGE_OBJECT	"https://www.mmca.go.kr/upload/exhibition/2025/06/2025061711274643617296.png"
        		//            또는       "/images/board/exhibition/monet_pond.jpg"
        		String[] parts = tmp_imageObject.split("/");
        		String imgObjectName = parts[parts.length-1]; // 이미지이름: "2025061711274643617296.png"
        		String imgObjectPath = String.join("/", Arrays.copyOfRange(parts, 0, parts.length - 1)) + "/"; // frontend 에서 경로명 사용에 맨 끝 "/"이 필요함 

        		List<BoardImgDB> boardImgList = new ArrayList<>();
        		BoardImgDB boardImg = new BoardImgDB();
        		int imgOrderNumber = 0; // 0, thumbnail: 전시의 경우
        		

        		// DB 서비스 호출
        		int resultBoardImgDB = 0;
        		//if (resultBoardDB > 0) { // ok
        			boardImg.setBoardNo(board.getBoardNo());         		
        			boardImg.setImgPath(imgObjectPath);
        			boardImg.setImgOrig(imgObjectName);
        			boardImg.setImgRename(imgObjectName ); // 같은 이미지이름, 임의할당
        			boardImg.setImgOrder(imgOrderNumber); // 0, thumbnail: 전시의 경우
        			
        			boardImgList.add(boardImg);
        			
        			resultBoardImgDB = jsonExhibitionService.jsonBoardImgInsert(boardImgList); // result:0(삽입 실패) ~ 5(성공횟수, 전시의 경우 1)         			
        			log.info("resultBoradImgDB : {}", resultBoardImgDB);        		
        		//}
        		
        		//log.info("여기까지 OK: BoardImgDB insert 이후");
        		
        		
        		///////////        		
        		// 3) Exhibition DTO에 맵핑하고, EXHIBITION DB에 insert 
        		// DTO 값 할당
        		ExhibitionDB exhibition = new ExhibitionDB();
        		//exhibition.setBoardNo(idxOffset); 
        		exhibition.setBoardNo(boardNo); 
        		
        		// ExhibitSubTitle의 글자수 200자 이상인 경우 slicing 필요 
        		String lenExhibitSubTitle = jsonExhibition.getExhibitSubTitle();
        		String slicedExhibitSubTitle = null;
        		if (lenExhibitSubTitle != null) {
        			slicedExhibitSubTitle = truncateByByte(lenExhibitSubTitle, 200 - " ...... ".getBytes(StandardCharsets.UTF_8).length);
        			slicedExhibitSubTitle += " ......";        			
        		}
        		exhibition.setExhibitSubTitle(slicedExhibitSubTitle);
        		exhibition.setExhibitSite( jsonExhibition.getExhibitLocation() );
        		exhibition.setExhibitDate( jsonExhibition.getExhibitDate() );
        		exhibition.setExhibitContact( jsonExhibition.getExhibitContact() );
        		exhibition.setExhibitAudience( jsonExhibition.getExhibitAudience() );
        		
        		// exhibition.setExhibitCharge()의 경우:
        		// Charge(int): String -> Integer 변환 필요
                Map<String, Integer> catChargeMap = new HashMap<>();
                catChargeMap.put("무료", 0);
                catChargeMap.put("0", 0);
                catChargeMap.put("2,000원", 2000);
                catChargeMap.put("02", 7777777);
                catChargeMap.put("과천 전시관람권 3,000원", 3000);
                catChargeMap.put("3,000원", 3000);
                catChargeMap.put("개별 프로그램 별 상이", 7777777);
                catChargeMap.put("2,000원 (덕수궁 입장료 별도)", 2000);
                catChargeMap.put("미정", 7777777);
                catChargeMap.put("-", 7777777);
                // 검색할 문자열
                String strCharge = jsonExhibition.getExhibitCharge();
                // 값 조회
                int convCharge = 0;
                if (catChargeMap.containsKey(strCharge)) {
                    convCharge = catChargeMap.get(strCharge);
                    System.out.println("'" + strCharge + "'의 값은: " + convCharge);
                    log.info("Charge: '{}'의 값은: {}", strCharge, convCharge);
                } else {
                	convCharge = 7777777; // 예매불가, 전시관 직접문의 경우의 입장료 코드
                    System.out.println("'" + strCharge + "'는 예매불가. 전시관에 직접문의 필요한 경우");
                    log.info("Charge: '{}'로 표기된 경우는 예매불가. 전시관에 직접문의 필요한 경우", strCharge);
                }       		
        		exhibition.setExhibitCharge(convCharge);
        		
        		// exhibition.setInstitutionNo()의 경우:
        		// InstitutionNo(int): from INSTITUTION 테이블 조회 (exhibitInstName을 키값으로)
        		String exhibitInstName = jsonExhibition.getExhibitInstitution();  // exhibitInstName은 InstitutionDB 필드명
        		int resultInstitutionNo = jsonExhibitionService.jsonInstitutionSelect(exhibitInstName); // 전시기관 코드 반환;
        		log.info("INSTITUTION 테이블에서 조회성공, InstitutionNo : {}", resultInstitutionNo);
        		exhibition.setInstitutionNo(resultInstitutionNo); //  전시기관명을 전시기관번호(institutionNo)로 조회

        		// exhibition.setGenreNo() 의 경우:
        		// GenreNo(int): from GENRE 테이블 조회 (genreName 을 키값으로)
        		String genreName = jsonExhibition.getExhibitGenre();
        		int resultGenreNo = jsonExhibitionService.jsonGenreSelect(genreName); // 장르 코드 반환; genreNo는  GenreDB 필드명       		
        		log.info("GENRE 테이블에서 조회성공, genreNo : {}", resultInstitutionNo);
        		exhibition.setGenreNo(resultGenreNo);
        		// DB 서비스 호출
        		int resultExhibitionDB = 0;
        		resultExhibitionDB = jsonExhibitionService.jsonExhibitionInsert(exhibition); 
        		log.info(" EXHIBITION 테이블 insert 결과 : {}", resultExhibitionDB);
        		

        		///////////
        		// 4) Author DTO에 맵핑하고, AUTHOR DB에 insert 
        		// DTO 값 할당
        		AuthorDB author = new AuthorDB(); 
        		String authorsString = jsonExhibition.getExhibitAuthor();
        		String separator = "\\s*,\\s*";  // 쉼표 기준 분리 + 공백 제거
        		int maxAuthors = 3;
        		
        		// authorsString == null인 경우 처리 ==> authorList에는 한 아이템 & setAuthorName(null)
        		List<AuthorDB> authorList = new ArrayList<>();		
        		if (authorsString != null) {
        			authorList = parseAuthors(authorsString, separator, maxAuthors, boardNo);
        		} else {
        			author.setAuthorName(null);
        			author.setBoardNo(boardNo);
        			authorList.add(author);
        		}
        		
        		// DB 서비스 호출       		
        		int resultAuthorDB = 0;
        		resultAuthorDB = jsonExhibitionService.jsonAuthorInsert(authorList); // result:0(삽입 실패)         			
        		log.info(" AUTHOR 테이블 insert 결과 : {}", resultAuthorDB);
        		
        		
        		///////////        		
        		// 5) Like DTO에 맵핑하고, LIKE DB에 insert 
        		// DTO 값 할당
        		LikeDB like = new LikeDB(); 
        		like.setBoardNo(idxOffset);
        		int likeCount = jsonExhibition.getLikeCount(); // 0 이상 9 이하 중 임의 수 할당되어 있음 따라서 memberNo = 12 ~ 21번 중에서 likeCount수만큼 random하게 뽑아 insert해준다.

        		int resultLikeDB = 0;
        		
        		if (likeCount == 0) {
        			resultLikeDB = 1; // 0일경우 삽입할 memberNo없으므로, 그냥 resultLikeDB 태그만 성공으로마크하고 넘어간다
        		} else {       			
        			List<LikeDB> likeList = randomLikeMemberNo(boardNo, likeCount); // like MemberNo List
        			resultLikeDB = jsonExhibitionService.jsonLikeInsert(likeList); // result:0(삽입 실패)  
        		}
        		
        		// DB 서비스 호출        		
        		log.info(" likeCount : {}", likeCount);  
        		log.info(" LIKE 테이블 insert 결과 : {}", resultLikeDB);        		
        		
        		///////////        		
        		// 6) Contributor DTO에 맵핑하고, CONTRIBUTOR DB에 insert 
        		// DTO 값 할당
        		ContributorDB contributor = new ContributorDB(); 
        		String contributorString = jsonExhibition.getExhibitContributor();       		
        		
        		contributor = parseContributor(contributorString, boardNo);
        		
        		// DB 서비스 호출        		
        		int resultContributorDB = 0;
        		resultContributorDB = jsonExhibitionService.jsonContributorInsert(contributor); // result:0(삽입 실패) ~ 5(성공횟수, 전시의 경우 1)         			
        		log.info(" CONTRIBUTOR 테이블 insert 결과 : {}", resultContributorDB);        		
        		
        		///////////////////////////////////////////////////////
				// 게시글 삽입 서비스 호출 결과 후처리
				String message = null;
				String path = "redirect:";
				if (resultBoardDB > 0 && resultBoardImgDB > 0 && resultExhibitionDB > 0
						&& resultAuthorDB > 0 && resultLikeDB > 0 && resultContributorDB > 0) {	// 게시글 삽입 성공시 			
					String successLocation = resultBoardDB + ", " + resultBoardImgDB + ", "
											+ resultExhibitionDB + ", " + resultAuthorDB + ", "
											+ resultLikeDB  + ", " + resultContributorDB; // logging successLocation (>1 : 성공, 0:실패)
					message = (idx + 1)  + "번째 게시글(boardNo=" + board.getBoardNo() + ")이 DB에 삽입 되었습니다: " + successLocation;
					log.info(message);

				} else {
					// 게시글 삽입 실패 시 
					String failureLocation = resultBoardDB + ", " + resultBoardImgDB + ", "
											+ resultExhibitionDB + ", " + resultAuthorDB + ", "
											+ resultLikeDB  + ", " + resultContributorDB; // // logging failureLocation (>1 : 성공, 0:실패)
					message = (idx + 1)  + "번째 게시글(boardNo=" + board.getBoardNo() + ") DB삽입 실패: " + failureLocation;
					log.info(message);
					
					continue; // 실패시, 다음 데이터 입력
				}  		
        		
				resultMessageList.add(message); // collecting INSERT result logs
        		//break; // for testing
        		
        	}
        	
        	log.info("insert result : {}", resultMessageList);
			
        } catch (Exception e) {
        	e.printStackTrace();
        }
	    
        return "common/main";        
	}	
	
	// AUTHOR DB 파싱 처리
    public static List<AuthorDB> parseAuthors(String authorsStr, String separator, int maxAuthors, int boardNo) {
        List<AuthorDB> result = new ArrayList<>();
        if (authorsStr == null || authorsStr.trim().isEmpty()) return result;

        String[] names = authorsStr.split(separator);  // 쉼표+공백 제거
        int total = names.length;

        if (total <= maxAuthors) {
            for (String name : names) {
                result.add(new AuthorDB(0, name, boardNo));  // authorNo는 DB에서 시퀀스처리
            }
        } else {
            for (int i = 0; i < maxAuthors - 1; i++) {
                result.add(new AuthorDB(0, names[i], boardNo));
            }
            String lastName = names[maxAuthors - 1];
            int remaining = total - maxAuthors;
            String modifiedLast = lastName + " 외 " + remaining + "명";
            result.add(new AuthorDB(0, modifiedLast, boardNo));
        }

        return result;
    }	    


    public static List<LikeDB> randomLikeMemberNo(int boardNo, int N) {
        if (N > 10 || N < 1) {
            throw new IllegalArgumentException("N must be between 1 and 10");
        }

        List<Integer> memberNos = new ArrayList<>();
        for (int i = 12; i <= 21; i++) { // 전시에서 사용한 memberNo:10(한국문화정보원), 11(나챗봇), 12 ~ 21번
            memberNos.add(i);
        }

        Collections.shuffle(memberNos); // 섞기

        List<LikeDB> result = new ArrayList<>();
        for (int i = 0; i < N; i++) { 	// 앞에서 N개 선택
            result.add(new LikeDB(boardNo, memberNos.get(i)));
        }

        return result;
    }

	public static ContributorDB parseContributor(String contributorString, int boardNo) {
		if (contributorString == null || contributorString.trim().isEmpty()) {
			return new ContributorDB(0, null, null, boardNo); // SEQ_CONTRIBUTOR_NO 가정(DB가 관리)
		}

		String[] parts = contributorString.split("/");
		String hostGroup = "";
		String supportGroup = "";

		if (parts.length == 1) {
			// "/"가 없고 둘 중 하나만 존재 => host group만 존재 가정
			if (contributorString.contains(",")) {
				hostGroup = contributorString.trim();
			} else {
				if (!contributorString.trim().isEmpty()) {
					hostGroup = contributorString.trim();
				} else {
					log.info("문제있는 contributor string 내용: {}", contributorString); // " / "를 입력한 경우
					hostGroup = "TBA"; // default
					//throw new IllegalArgumentException("Invalid format: expected at least one '/' or one group.");					
				}
			}
		} else if (parts.length == 2) {
			hostGroup = parts[0].trim();
			supportGroup = parts[1].trim();
		} else {
			log.info("Invalid format: more than one '/' found."); // " / "를 입력한 경우
			hostGroup = "TBA"; // default
			//throw new IllegalArgumentException("Invalid format: more than one '/' found.");
		}

		// 공백 제거
		hostGroup = removeExtraSpaces(hostGroup);
		supportGroup = removeExtraSpaces(supportGroup);

		// 둘 다 비어 있으면 예외(host와 support동시에 비어있을때 default값세팅)
		if (hostGroup.isEmpty() && supportGroup.isEmpty()) {
			hostGroup = "TBA"; // default
			//throw new IllegalArgumentException("Both host and support groups cannot be empty.");
		}

		return new ContributorDB(0, hostGroup, supportGroup, boardNo); // SEQ_CONTRIBUTOR_NO 가정(DB가 관리)
	}

	private static String removeExtraSpaces(String str) {
		if (str == null || str.trim().isEmpty()) return "";
		String[] tokens = str.split(",");
		return Arrays.stream(tokens)
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.collect(Collectors.joining(", "));
	}
	
	
	public static String truncateByByte(String input, int maxBytes) {
	    if (input == null) return null;

	    byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
	    if (bytes.length <= maxBytes) return input;

	    // 바이트 길이가 maxBytes를 넘을 경우 잘라서 다시 문자열로 생성
	    int endIndex = maxBytes;
	    while (endIndex > 0 && (bytes[endIndex] & 0xC0) == 0x80) { // 멀티바이트 연속	중간 바이트	10xxxxxx ← "0x80"	    	
	        endIndex--;
	    }
	    return new String(bytes, 0, endIndex, StandardCharsets.UTF_8);
	}	
}
