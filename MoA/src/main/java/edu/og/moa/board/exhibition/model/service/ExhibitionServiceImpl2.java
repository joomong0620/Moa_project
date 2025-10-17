package edu.og.moa.board.exhibition.model.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.og.moa.board.exhibition.controller.ExhibitionController;
import edu.og.moa.board.exhibition.controller.JsonExhibitionController;
import edu.og.moa.board.exhibition.model.dao.ExhibitionMapper2;
import edu.og.moa.board.exhibition.model.dto.AuthorDB;
import edu.og.moa.board.exhibition.model.dto.BoardDB;
import edu.og.moa.board.exhibition.model.dto.BoardImgDB;
import edu.og.moa.board.exhibition.model.dto.ContributorDB;
import edu.og.moa.board.exhibition.model.dto.Exhibition;
import edu.og.moa.board.exhibition.model.dto.ExhibitionDB;
import edu.og.moa.board.exhibition.model.dto.GenreDB;
import edu.og.moa.board.exhibition.model.dto.InstitutionDB;
import edu.og.moa.board.exhibition.model.exception.AuthorInsertException;
import edu.og.moa.board.exhibition.model.exception.ContributorInsertException;
import edu.og.moa.board.exhibition.model.exception.ExhibitionInsertException;
import edu.og.moa.board.exhibition.model.exception.GenreInsertException;
import edu.og.moa.board.exhibition.model.exception.InstitutionInsertException;
import edu.og.moa.board.freeboard.model.exception.FileUploadException;
import edu.og.moa.common.utility.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@PropertySource("classpath:/config.properties") // config.properties 내용을 현재 클래스에서 사용
public class ExhibitionServiceImpl2 implements ExhibitionService2{

	@Value("${pyy.exhibition.webpath}") // config.properties 내용을 현재 클래스에서 사용
	private String webPath;
	
	@Value("${pyy.exhibition.location}")  // config.properties 내용을 현재 클래스에서 사용
	private String filePath;
	
	@Autowired
	private ExhibitionMapper2 mapper; // alt+shift+r: dao -> mapper로	
	
//	@Autowired
//	private JsonExhibitionController jsonEC; // 필드 주입 (간단, 테스트와 유지보수에는 불리)
//	
//	// 생성자 주입 방식: final로 선언할 수 있어 불변성(immutability) 보장, 단위 테스트 시 명확한 주입이 가능, 주입받지 못했을 때 컴파일 타임에 잡을 수 있음
//    private final JsonExhibitionController jsonEC;
//    public ExhibitionServiceImpl2(JsonExhibitionController jsonEC) {
//        this.jsonEC = jsonEC;
//    }
	@Autowired
	private JsonExhibitionService jsonExhibitionService;
	
	// 게시글 삭제
	@Override
	public int exhibitionDelete(int exhibitNo) {
		// 
		return mapper.exhibitionDelete(exhibitNo);
	}


	// 게시글 삽입
	@Transactional(rollbackFor = Exception.class)
	@Override
	//public int exhibtionInsert(Exhibition exhibition, List<MultipartFile> images, String webPath, String filePath)
	public int exhibtionInsert(Exhibition exhibition, List<MultipartFile> images)
			throws IllegalStateException, IOException {
		// 이제 여기서 exhibition에 담긴 정보를 각 DB에 들어갈 정보로 갈라서, 각 DB별 그룹핑하여 json-to-DB 로딩경우처럼 순차로 진행하되, error경우는 롤백한다.
		
		// exhibition에 담긴 정보를 각 DB에 들어갈 정보로 나누어 DB 하나씩 순차처리
		// BoardDB, BoardImgDB, ExhibitionDB, AuthorDB, LikeDB, Contributor (+ GenreDB, InstitutionDB)
		
		////////////////////////////////////// BoardDB
		// BoardDB DTO에 맵핑하고, BOAR DB에 insert 
		BoardDB board = new BoardDB();
		
		// 0. XSS 방지 처리: <script> 집어넣을 경우 무력화 ==> Thymeleaf에서는 필요없다(태그해석 할지말지 선택은 th:utext, [(...)]으로 지정가능)
		// 제목만 XSS 방지처리:
		board.setBoardTitle(Util.XSSHandling(exhibition.getExhibitTitle())); // title 변환시킨후 DB에 저장 ==> 타이틀을 읽어야하는 태그가 BoardDetail.html에 있기 때문
		
		// ExhibitContent의 글자수 > 4000 이상인 경우 slicing 필요
		String lenExhibitContent = exhibition.getExhibitContent();
		String slicedExhibitContent = null;
		if (lenExhibitContent != null) {
			slicedExhibitContent = JsonExhibitionController.truncateByByte(lenExhibitContent, 4000 - " ...... ".getBytes(StandardCharsets.UTF_8).length);
			//slicedExhibitContent = jsonEC.truncateByByte(lenExhibitContent, 4000 - " ...... ".getBytes(StandardCharsets.UTF_8).length);
			slicedExhibitContent += " ......";
		}        		
		
		
		board.setBoardContent( slicedExhibitContent );
		//board.setBCreateDate(exhibition.getExhibitCreateDate());// 날짜 형식 "2025-06-17 00:10:02"
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date curTime = new Date();
		String currentTime = sdf.format(curTime);
		log.info("currentTime format check (2025-06-17 00:10:02 ?): {}", currentTime);
		//board.setBCreateDate(currentTime);// 날짜 형식 "2025-06-17 00:10:02"
		board.setBCreateDate(null);// 날짜 형식 "2025-06-17 00:10:02"
		//board.setBCreateDate(jsonExhibition.getExhibitCreateDate());// 날짜 형식 "2025-06-17 00:10:02"
		//board.setBCreateDate('DEFAULT');// 디폴트 없음
		board.setBUpdateDate(null);
		
		board.setBoardCount(0); 		// 처음 입력시에는 exhibition.getReadCount() = 0   
		board.setMemberNo(exhibition.getMemberNo()); 			// 게시글 입력 회원의 회원번호
		board.setCommunityCode(exhibition.getCommunityCode()); 	// 3번 전시게시판 값 할당
		
		// DB 서비스 호출	
		// SelectKey를 이용하여 가용한 시퀀스값 먼저 조회하여, 그것을 boardNo값으로 입력 
		int boardNo = jsonExhibitionService.jsonBoardInsertViaSelectKey(board); // 0 실패, 1성공
			
		
		if (boardNo == 0) {
			return 0; // 실패시 서비스 종료 (밑에 코드 수행할 필요 없다
		} else {
			log.info("BOARD 테이블에서 삽입 성공, boardNo : {}", boardNo);

		}
			
		
		// mapper.xml에서 selectKey 태그로 인해 boardNo에 세팅된 값
		// (useGeneratedKeys="true" 와 selectKey-tag를 이용하여 조회된 (insert용)게시글번호로, 이번호로 insert진행되었음; board가 얕은복사이기에 가능) 얻어오기
		boardNo = board.getBoardNo();
		
		////////////////////////////////////// BoardImgDB
		// BoardImgDB DTO에 맵핑하고, BOARD_IMG DB에 insert 
		// 2. 게시글 삽입 성공 시
		//    업로드된 이미지가 있다면 BOARD_IMG 테이블에 삽입하는 DAO호출
		int resultBoardImgDB = 0; // 0 실패, 1 성공
		if (boardNo != 0) {
			// List<MultipartFile> images (전시의 경우 1개)
			// -> 업로드된 파일이 담긴 객체
			// -> 단, 업로드된 파일이 없어도 MultipartFile 객체는 존재(5개, 파일 없을때는 size=0 또는 파일명(getOriginalFileName())이 "")
			
			// 실제로 업로드된 파일의 정보를 기록할 List
			List<BoardImgDB> uploadList = new ArrayList<BoardImgDB>();
			
			// images에 담겨있는 파일 중 실제로 업로드된 파일만 분류 
			for(int i=0; i<images.size(); i++) { // 이미지 파일 있으나 없으나, images.size()=이 기본(전시의 경우)
				
				// i번째 요소에 업로드한 파일이 있다면
				if (images.get(i).getSize() > 0) { // 업로드한 이미지 있다.
					// img에 파일 정보를 담아서 uploadList에 추가
					BoardImgDB boardImg = new BoardImgDB();
					
					boardImg.setImgPath(webPath); // 웹 접근 경로
					
					// 파일 원본명
					String fileName = images.get(i).getOriginalFilename(); // 파일 원본명 from 리스트
					
					// 파일 변경명 img에 세팅
					boardImg.setImgRename(Util.fileRename(fileName));
					
					// 파일 원본명 img에 세팅
					boardImg.setImgOrig(fileName);
					
					// 다른 필요한 값들 img에 세팅
					boardImg.setImgOrder(i); 	 // 이미지 순서
					boardImg.setBoardNo(boardNo); // 게시글 번호
					
					uploadList.add(boardImg);
					
				}
				
			} // 분류 for문 종료 -> for문 각 i마다 DAO호출 않고, 한번에 images.size()을 한번 sql문으로 얻어오기(전시의 경우, images.size() = 1)
			
			// 분류 작업 후 uploadList가 비어있지 않은 경우
			// == 업로드한 파일이 존재
			if(!uploadList.isEmpty()) {
				
				// BOARD_IMG 테이블에 insert 하기
				resultBoardImgDB = jsonExhibitionService.jsonBoardImgInsert(uploadList); // 이것까지 성공해야 commit by @Transactional() => 0 실패, 1 성공
				
				// result == 성공한 행의 개수
				// uploadList.size() = 1   
				// 위 둘이 다르면 -> 부분 성공(전시의 경우는 이미지하나뿐이므로 실패), 같으면 -> 전체 성공
				//
				// 삽입된 행의 갯수(result)와 uploadList의 개수(uploadList.size())가 같다면
				// == 전체 insert 성공
				if (resultBoardImgDB == uploadList.size()) { // 전체 성공 or 부분 성공/전체 실패 (전시의 경우 포스터 1개 이므로 0 또는 1)
					
					// 서버에 파일 저장(transferTo())
					// cf) myPageServiceImpl.java에서: if(rename != null) profileImage.transferTo(new File(filePath + rename));
					//     profileImage -> MultipartFile profileImage (MultipartFile 타입)
					//
					// images: 실제 파일이 담긴 객체 리스트 (List<MultipartFile> images ) -> tranferTo()는 여기서...
					// (업로드 안된 인덱스는 빈칸으로)
					//
					// uploadList : 업로드된 파일의 정보 리스트 (List<BoardImage> uploadList)
					// (원본명, 변경명, 이미지 순서, 경로, 게시글 번호)
					//
					// images 업로드된 인덱스 == uploadList의 순서
					// ex) images 5개 -> uploadList 3개 ->그러면 for문은 3번만 돌며 upload해주면 된다.
					
					for (int i=0; i<uploadList.size(); i++) {
						// upload된 index번호의 이미지만 upload할것이므로, 그에 대응하는 images의 이미지 얻어와야 함
						//
						// 이미지 순서
						int index = uploadList.get(i).getImgOrder(); //
						
						// 변경명
						String rename = uploadList.get(i).getImgRename();
						//images.get(index).transferTo(new File(filePath + 변경명)) throws IllegalStateException, IOException; 
						images.get(index).transferTo(new File(filePath + rename));  // index에 해당하는 images[index]만 서버로 옮겨준다(서버에 저장한다)
					}
					
					log.info("BOARD_IMG 테이블에서 삽입(&파일저장) 성공, resultBoardImgDB : {}", resultBoardImgDB);
					
				} else { // 일부 또는 전체 insert 실패
					// * 웹 서비스 수행 중 1개라도 실패하면 전체 실패 *
					// -> rollback 필요 (but, @Transactional rollback은 exception이 발생해야만 rollback진행
					//
					// @Transactional (rollbackFor = Exception.class)
					// -> 예외가 발생해야만 롤백한다.
					
					// 따라서 예외를 강제로 발생시켜서 rollback 해야 한다.
					// -> 사용자 정의 예외 (강제)생성 by "throw"
					throw new FileUploadException(); // 강제 예외 발생 시키는 구문 -> 이제 @Transactional에서 rollback한다.
						
				}
					
			}			
			
		}
		
		
		////////////////////////////////////////////////////// GenreDB (GenreDB 보다 먼저해줘야 GenreDB.genreNo 값 세팅가능) 
		GenreDB genreDB = new GenreDB();
		// exhibitionDB.setGenreNo() 의 경우:
		// GenreNo(int): from GENRE 테이블 조회 (genreName 을 키값으로)해서 있으면 그 장르번호, 없으면 insert하면서 장르번호(selectKey) 얻어온다.
		
		String genreName = exhibition.getExhibitGenre();
		genreDB.setGenreName(genreName); // genreNo는 아래 insert할때 generated Key값으로 채워넣자
		
		int resultGenreDB = 0; // 0 실패, 1 성공
		int genreNo = 0;
		if (boardNo != 0 && resultBoardImgDB > 0) { 
			
			// 먼저 select count(*)
			int resSelCnt = mapper.genreSelectCount(genreName); // genreName의 genreNo 수 세기: 있으면 1, 없으면 0
			if (resSelCnt > 0) {
				genreNo = jsonExhibitionService.jsonGenreSelect(genreName); // 장르 넘버 반환; genreNo는  GenreDB 필드명 
				resultGenreDB = genreNo;
				log.info("GENRE 테이블에서 조회성공, genreNo : {}", genreNo);
			} else {
				resultGenreDB = mapper.genreInsert(genreDB);
				if (resultGenreDB > 0) {
					log.info("GENRE 테이블에서 삽입성공, resultGenreDB : {}", resultGenreDB);
					genreNo = genreDB.getGenreNo();
				} else {
					throw new GenreInsertException(); // 강제 예외 발생 시키는 구문 -> 이제 @Transactional에서 rollback한다.
				}
			}
			
		}			
		
		
		////////////////////////////////////////////////////// InstitutionDB (ExhibitionDB 보다 먼저해줘야 ExhibitionDB.institutionNo 값 세팅가능) 
		InstitutionDB institutionDB = new InstitutionDB();
		// exhibitionDB.setInstitutionNo()의 경우:
		// InstitutionNo(int): from INSTITUTION 테이블 조회 (exhibitInstName을 키값으로)해서 있으면 그 기관번호, 없으면 insert하면서 기관번호(selectKey) 얻어온다.
		String exhibitInstName = exhibition.getExhibitInstitution();  // exhibitInstName은 InstitutionDB 필드명
		String exhibitContact = exhibition.getExhibitContact();
		institutionDB.setExhibitInstName(exhibitInstName);
		institutionDB.setExhibitInstTel(exhibitContact);
		int resultInstitutionDB = 0; // 0 실패, 1 성공
		int institutionNo = 0;
		//Map<String, Object> institutionMap = new HashMap<>();
		//institutionMap.put("exhibitInstName", exhibitInstName);
		//institutionMap.put("exhibitInstTel", exhibitContact);
		if (boardNo != 0 && resultBoardImgDB > 0 && resultGenreDB > 0) { 
			// 먼저 select count(*)
			int resSelCnt = mapper.institutionSelectCount(exhibitInstName); // exhibitInstName의 institutionNo 갯 수 세기: 있으면 1, 없으면 0
			if (resSelCnt > 0) {
				institutionNo = jsonExhibitionService.jsonInstitutionSelect(exhibitInstName); // 전시기관 번호 반환; 
				resultInstitutionDB = institutionNo;
				log.info("INSTITUTION 테이블에서 조회성공, institutionNo : {}", institutionNo);				
			} else {
				resultInstitutionDB = mapper.institutionInsert(institutionDB);
				if (resultInstitutionDB > 0) {
					log.info("INSTITUTION 테이블에서 삽입 성공, resultInstitutionDB : {}", resultInstitutionDB);
					institutionNo = institutionDB.getInstitutionNo();
				} else {
					throw new InstitutionInsertException(); // 강제 예외 발생 시키는 구문 -> 이제 @Transactional에서 rollback한다.
				}				
			}
		}
		
		
		////////////////////////////////////////////////////// ExhibitionDB
		// 3) ExhibitionDB DTO에 맵핑하고, EXHIBITION DB에 insert 
		// DTO 값 할당
		ExhibitionDB exhibitionDB = new ExhibitionDB();
		exhibitionDB.setBoardNo(boardNo); 
		
		// ExhibitSubTitle의 글자수 200자 이상인 경우 slicing 필요 
		String lenExhibitSubTitle = exhibition.getExhibitSubTitle();
		String slicedExhibitSubTitle = null;
		if (lenExhibitSubTitle != null) {
			slicedExhibitSubTitle = JsonExhibitionController.truncateByByte(lenExhibitSubTitle, 200 - " ...... ".getBytes(StandardCharsets.UTF_8).length);
			slicedExhibitSubTitle += " ......";        			
		}
		exhibitionDB.setExhibitSubTitle(slicedExhibitSubTitle);
		
		exhibitionDB.setExhibitSite( exhibition.getExhibitLocation() );
		exhibitionDB.setExhibitDate( exhibition.getExhibitDate() );
		exhibitionDB.setExhibitContact( exhibition.getExhibitContact() );
		exhibitionDB.setExhibitAudience( exhibition.getExhibitAudience() );
		
		// exhibition.setExhibitCharge()의 경우:
		// 사용자로부터 숫자만("2000"문자열타입) 입력받을 것이므로 문자열을 숫자로 변환,
		// 그리고, "3000원", "무료", "관람료 없음" 같은 경우들 고려
        String strCharge = exhibition.getExhibitCharge();
        Pattern pattern = Pattern.compile("\\d+"); // 숫자 패턴
        Matcher matcher = pattern.matcher(strCharge);

        String parsedNumberString = null;
        int convCharge = 0;
        if (matcher.find()) { // 추출 숫자 맨 처음꺼 하나만
            parsedNumberString = matcher.group();
            convCharge = Integer.parseInt(parsedNumberString);
        } else {
        	if (strCharge.contains("무료") || strCharge.contains("없음") ) {
        		convCharge = 0;
        	} else {
        		convCharge = 7777777; // 예매불가, 전시관 직접문의 경우의 입장료 코드
                System.out.println("'" + strCharge + "'는 예매불가. 전시관에 직접문의 필요한 경우");
        	}
        }
        exhibitionDB.setExhibitCharge(convCharge);
		
        exhibitionDB.setGenreNo(genreNo);	
		exhibitionDB.setInstitutionNo(institutionNo); //  전시기관명을 전시기관번호(institutionNo)로 조회		

		// DB 서비스 호출		
		int resultExhibitionDB = 0; // 0 실패, 1 성공
		if (boardNo != 0 && resultBoardImgDB > 0 && resultGenreDB > 0 && resultInstitutionDB > 0) { 
			resultExhibitionDB = jsonExhibitionService.jsonExhibitionInsert(exhibitionDB); 
			if (resultExhibitionDB > 0) {
				log.info("EXHIBITION 테이블에서 삽입 성공, resultExhibitionDB : {}", resultExhibitionDB);
			} else {
				throw new ExhibitionInsertException(); // 강제 예외 발생 시키는 구문 -> 이제 @Transactional에서 rollback한다.
			}
		}
			
			
		////////////////////////////////////////////////////// AuthorDB
		// DTO 값 할당
		AuthorDB author = new AuthorDB(); 
		String authorsString = exhibition.getExhibitAuthor();
		String separator = "\\s*,\\s*";  // 쉼표 기준 분리 + 공백 제거
		int maxAuthors = 3;
		
		// authorsString == null인 경우 처리 ==> authorList에는 한 아이템 & setAuthorName(null)
		List<AuthorDB> authorList = new ArrayList<>();		
		if (authorsString != null) {
			authorList = JsonExhibitionController.parseAuthors(authorsString, separator, maxAuthors, boardNo);
		} else {
			author.setAuthorName(null);
			author.setBoardNo(boardNo);
			authorList.add(author);
		}
		
		// DB 서비스 호출       		
		int resultAuthorDB = 0; // 0 실패, 1 성공
		if (boardNo != 0 && resultBoardImgDB > 0 && resultGenreDB > 0 && resultInstitutionDB > 0
				&& resultExhibitionDB > 0) { 
			resultAuthorDB = jsonExhibitionService.jsonAuthorInsert(authorList); // result:0(삽입 실패)   
			if (resultAuthorDB > 0) {
				log.info(" AUTHOR 테이블 insert 성공, resultAuthorDB: {}", resultAuthorDB);		
			} else {
				throw new AuthorInsertException(); // 강제 예외 발생 시키는 구문 -> 이제 @Transactional에서 rollback한다.
			}
		}		
			

		////////////////////////////////////////////////////// ContributorDB	
		// DTO 값 할당
		ContributorDB contributor = new ContributorDB(); 
		String contributorString = exhibition.getExhibitContributor();       		
		
		contributor = JsonExhibitionController.parseContributor(contributorString, boardNo);
		
		// DB 서비스 호출        		
		
		int resultContributorDB = 0; // 0 실패, 1 성공
		if (boardNo != 0 && resultBoardImgDB > 0 && resultGenreDB > 0 && resultInstitutionDB > 0
				&& resultExhibitionDB > 0 && resultAuthorDB > 0
				) { 
			resultContributorDB = jsonExhibitionService.jsonContributorInsert(contributor); // result:0(삽입 실패) 	
			if (resultContributorDB > 0) {
				log.info(" CONTRIBUTOR 테이블 insert 성공, resultContributorDB: {}", resultContributorDB);     						
			} else {
				throw new ContributorInsertException(); // 강제 예외 발생 시키는 구문 -> 이제 @Transactional에서 rollback한다.
			}
		}			
		
		//return 0;
		return boardNo;
	}


	// 게시글 수정
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int exhibitionUpdate(Exhibition exhibition, List<MultipartFile> images, String deleteList) throws IllegalStateException, IOException {
		// 흐름은 exhibitionInsert와 같으나, 달라진 점은
		// 1. insert -> update로 바꾸고, form-tag에서 넘겨주는 exhibition 필드값들만 업데이트 해준다.(이전값과 같은지 테스트않고, 입력값은 모두 없데이트)
		// 2. 그리고 있는 애들 업데이트하는 관계로 사용자정의 exception을 발동할 필요는 없으나, 해도 무방?
		// 3. deleteList는 전시에 경우 무시: 전시에서는 사용자 입력일때 only-one 전시포스터가 항상 있어야 한다.
		// 이제 여기서 exhibition에 담긴 정보를 각 DB에 들어갈 정보로 갈라서, 각 DB별 그룹핑하여 json-to-DB 로딩경우처럼 순차로 진행하되, error경우는 롤백한다.
		
		// exhibition에 담긴 정보를 각 DB에 들어갈 정보로 나누어 DB 하나씩 순차처리
		// BoardDB, BoardImgDB, ExhibitionDB, AuthorDB, LikeDB, Contributor (+ GenreDB, InstitutionDB)
		
		////////////////////////////////////// BoardDB
		// BoardDB DTO에 맵핑하고, BOARD DB update 
		BoardDB board = new BoardDB();
		
		board.setBoardNo(exhibition.getExhibitNo()); // boardNo === exhibitNo
		// 0. XSS 방지 처리: <script> 집어넣을 경우 무력화 ==> Thymeleaf에서는 필요없다(태그해석 할지말지 선택은 th:utext, [(...)]으로 지정가능)
		// 제목만 XSS 방지처리:
		board.setBoardTitle(Util.XSSHandling(exhibition.getExhibitTitle())); // title 변환시킨후 DB에 저장 ==> 타이틀을 읽어야하는 태그가 BoardDetail.html에 있기 때문
		
		// ExhibitContent의 글자수 > 4000 이상인 경우 slicing 필요
		String lenExhibitContent = exhibition.getExhibitContent();
		String slicedExhibitContent = null;
		if (lenExhibitContent != null) {
			slicedExhibitContent = JsonExhibitionController.truncateByByte(lenExhibitContent, 4000 - " ...... ".getBytes(StandardCharsets.UTF_8).length);
			//slicedExhibitContent = jsonEC.truncateByByte(lenExhibitContent, 4000 - " ...... ".getBytes(StandardCharsets.UTF_8).length);
			slicedExhibitContent += " ......";
		}        		
		
		
		board.setBoardContent( slicedExhibitContent );
		// update에서는 creation-date은 이미 생성된 시간으로 고정해 놓는다
		board.setBCreateDate(exhibition.getExhibitCreateDate());// 날짜 형식 "2025-06-17 00:10:02"; 최최생성날짜는 계속 고정된 값을 가지고 있어야 하므로 기존값 그대로 쓴다.
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date updTime = new Date();
		String updateTime = sdf.format(updTime);
		log.info("currentTime format check (2025-06-17 00:10:02 ?): {}", updateTime); // 2025-10-17 15:55:26
		//board.setBUpdateDate(null);// 날짜 형식 "2025-06-17 00:10:02"
		//board.setBUpdateDate('DEFAULT');// 디폴트 없음		
		board.setBUpdateDate(updateTime); // 업데이트 시간은 표시해 줘야함
		
		//board.setBoardCount(0); 		// 처음 입력시에는 exhibition.getReadCount() = 0   
		//board.setMemberNo(exhibition.getMemberNo()); 			// 게시글 입력 회원의 회원번호
		//board.setCommunityCode(exhibition.getCommunityCode()); 	// 3번 전시게시판 값 할당
		
		// DB 서비스 호출	
		// SelectKey를 이용하여 가용한 시퀀스값 먼저 조회하여, 그것을 boardNo값으로 입력 
		//int boardNo = jsonExhibitionService.jsonBoardInsertViaSelectKey(board); // 0 실패, 1성공
		int resCntBoardDB = mapper.updateBoardDB(board); // 0 실패, 1성공
			
		
		if (resCntBoardDB == 0) {
			return 0; // 실패시 서비스 종료 (밑에 코드 수행할 필요 없다
		} else {
			log.info("BOARD 테이블 수정 성공, resCntBoardDB : {}", resCntBoardDB);

		}
			
		
		// mapper.xml에서 selectKey 태그로 인해 boardNo에 세팅된 값
		// (useGeneratedKeys="true" 와 selectKey-tag를 이용하여 조회된 (insert용)게시글번호로, 이번호로 insert진행되었음; board가 얕은복사이기에 가능) 얻어오기
		int boardNo = board.getBoardNo(); // exhibitNo === boardNo (exhibition.getExhibitNo())
		
		////////////////////////////////////// BoardImgDB
		// BoardImgDB DTO에 맵핑하고, BOARD_IMG DB update 
		// 2. 게시글 삽입 성공 시
		//    업로드된 이미지가 있다면 BOARD_IMG 테이블에 수정하는 DAO호출
		int resCntBoardImgDB = 0; // 0 실패, 1 성공
		if (resCntBoardDB != 0) {
			// List<MultipartFile> images (전시의 경우 1)
			// -> 업로드된 파일이 담긴 객체
			// -> 단, 업로드된 파일이 없어도 MultipartFile 객체는 존재(5개, 파일 없을때는 size=0 또는 파일명(getOriginalFileName())이 "")
			
			// 실제로 업로드된 파일의 정보를 기록할 List
			List<BoardImgDB> uploadList = new ArrayList<BoardImgDB>();
			
			// images에 담겨있는 파일 중 실제로 업로드된 파일만 분류 
			for(int i=0; i<images.size(); i++) { // 이미지 파일 있으나 없으나, images.size()=이 기본(전시의 경우)
				
				// i번째 요소에 업로드한 파일이 있다면
				if (images.get(i).getSize() > 0) { // 업로드한 이미지 있다.
					// img에 파일 정보를 담아서 uploadList에 추가
					BoardImgDB boardImg = new BoardImgDB();
					
					boardImg.setBoardNo(boardNo);
					boardImg.setImgPath(webPath); // 웹 접근 경로
					
					// 파일 원본명
					String fileName = images.get(i).getOriginalFilename(); // 파일 원본명 from 리스트
					
					// 파일 변경명 img에 세팅
					boardImg.setImgRename(Util.fileRename(fileName));
					
					// 파일 원본명 img에 세팅
					boardImg.setImgOrig(fileName);
					
					// 다른 필요한 값들 img에 세팅
					boardImg.setImgOrder(i); 	 // 이미지 순서
					boardImg.setBoardNo(boardNo); // 게시글 번호
					
					uploadList.add(boardImg);
					
				}
				
			} // 분류 for문 종료 -> for문 각 i마다 DAO호출 않고, 한번에 images.size()을 한번 sql문으로 얻어오기(전시의 경우, images.size() = 1)
			
			// 분류 작업 후 uploadList가 비어있지 않은 경우
			// == 업로드한 파일이 존재
			if(!uploadList.isEmpty()) {
				
				// BOARD_IMG 테이블에 insert 하기
				resCntBoardImgDB = mapper.updateBoardImgDB(uploadList); // 이것까지 성공해야 commit by @Transactional() => 0 실패, 1 성공
				
				// result == 성공한 행의 개수
				// uploadList.size() = 1   
				// 위 둘이 다르면 -> 부분 성공(전시의 경우는 이미지하나뿐이므로 실패), 같으면 -> 전체 성공
				//
				// 삽입된 행의 갯수(result)와 uploadList의 개수(uploadList.size())가 같다면
				// == 전체 insert 성공
				if (resCntBoardImgDB == uploadList.size()) { // 전체 성공 or 부분 성공/전체 실패 (전시의 경우 포스터 1개 이므로 0 또는 1)
					
					// 서버에 파일 저장(transferTo())
					// cf) myPageServiceImpl.java에서: if(rename != null) profileImage.transferTo(new File(filePath + rename));
					//     profileImage -> MultipartFile profileImage (MultipartFile 타입)
					//
					// images: 실제 파일이 담긴 객체 리스트 (List<MultipartFile> images ) -> tranferTo()는 여기서...
					// (업로드 안된 인덱스는 빈칸으로)
					//
					// uploadList : 업로드된 파일의 정보 리스트 (List<BoardImage> uploadList)
					// (원본명, 변경명, 이미지 순서, 경로, 게시글 번호)
					//
					// images 업로드된 인덱스 == uploadList의 순서
					// ex) images 5개 -> uploadList 3개 ->그러면 for문은 3번만 돌며 upload해주면 된다.
					
					for (int i=0; i<uploadList.size(); i++) {
						// upload된 index번호의 이미지만 upload할것이므로, 그에 대응하는 images의 이미지 얻어와야 함
						//
						// 이미지 순서
						int index = uploadList.get(i).getImgOrder(); //
						
						// 변경명
						String rename = uploadList.get(i).getImgRename();
						//images.get(index).transferTo(new File(filePath + 변경명)) throws IllegalStateException, IOException; 
						images.get(index).transferTo(new File(filePath + rename));  // index에 해당하는 images[index]만 서버로 옮겨준다(서버에 저장한다)
					}
					
					log.info("BOARD_IMG 테이블에서 수정(&파일저장) 성공, resCntBoardImgDB : {}", resCntBoardImgDB);
					
				} else { // 일부 또는 전체 insert 실패
					// * 웹 서비스 수행 중 1개라도 실패하면 전체 실패 *
					// -> rollback 필요 (but, @Transactional rollback은 exception이 발생해야만 rollback진행
					//
					// @Transactional (rollbackFor = Exception.class)
					// -> 예외가 발생해야만 롤백한다.
					
					// 따라서 예외를 강제로 발생시켜서 rollback 해야 한다.
					// -> 사용자 정의 예외 (강제)생성 by "throw"
					throw new FileUploadException(); // 강제 예외 발생 시키는 구문 -> 이제 @Transactional에서 rollback한다.
						
				}
					
			}			
			
		}
		
		
		////////////////////////////////////////////////////// GenreDB (ExhibitionDB 보다 먼저해줘야 GenreDB.genreNo 값 세팅가능) 
		GenreDB genreDB = new GenreDB();
		// exhibitionDB.setGenreNo() 의 경우:
		// GenreNo(int): from GENRE 테이블 조회 (genreName 을 키값으로)해서 있으면 그 장르번호로 genreName update, 없으면 그 장르번호로 genreName insert; 각경우 실패시 사용자 에러생성.
		
		String genreName = exhibition.getExhibitGenre();
		genreDB.setGenreName(genreName); // genreNo는 아래 insert할때 generated Key값으로 채워넣자
		
		int resCntGenreDB = 0; // 0 실패, 1 성공
		int genreNo = 0;
		if (resCntBoardDB != 0 && resCntBoardImgDB > 0) { 
			
			// 먼저 select count(*)
			int resSelCnt = mapper.genreSelectCount(genreName); // genreName의 genreNo 수 세기: 있으면 1, 없으면 0
			if (resSelCnt > 0) {
				genreNo = jsonExhibitionService.jsonGenreSelect(genreName); // 장르 넘버 반환; genreNo는  GenreDB 필드명 
				resCntGenreDB = genreNo;
				log.info("GENRE 테이블에서 조회성공, genreNo : {}", genreNo);
				
				// 이미 genreName에 genreNo있으면 더 할게 없는데, 이건 그냥 반복(나중에 쓸일 위해)
				resCntGenreDB = mapper.updateGenreDB(genreNo);
				if (resCntGenreDB > 0) {
					log.info("GENRE 테이블에서 수정성공, resCntGenreDB : {}", resCntGenreDB);
				} else {
					// 수정실패
					throw new GenreInsertException(); // 강제 예외 발생 시키는 구문 -> 이제 @Transactional에서 rollback한다.
				}
			} else {
				resCntGenreDB = mapper.genreInsert(genreDB);
				if (resCntGenreDB > 0) {
					log.info("GENRE 테이블에 새로 삽입성공, resCntGenreDB : {}", resCntGenreDB);
					genreNo = genreDB.getGenreNo();
				} else {
					// 새장르번호 삽입실패
					throw new GenreInsertException(); // 강제 예외 발생 시키는 구문 -> 이제 @Transactional에서 rollback한다.
				}
			}
			
		}			
		
		
		////////////////////////////////////////////////////// InstitutionDB (ExhibitionDB 보다 먼저해줘야 ExhibitionDB.institutionNo 값 세팅가능) 
		InstitutionDB institutionDB = new InstitutionDB();
		// exhibitionDB.setInstitutionNo()의 경우:
		// InstitutionNo(int): from INSTITUTION 테이블 조회 (exhibitInstName을 키값으로)해서 있으면 그 기관번호, 없으면 insert하면서 기관번호(selectKey) 얻어온다.
		String exhibitInstName = exhibition.getExhibitInstitution();  // exhibitInstName은 InstitutionDB 필드명
		String exhibitContact = exhibition.getExhibitContact();
		institutionDB.setExhibitInstName(exhibitInstName);
		institutionDB.setExhibitInstTel(exhibitContact);
		int resCntInstitutionDB = 0; // 0 실패, 1 성공
		int institutionNo = 0;
		//Map<String, Object> institutionMap = new HashMap<>();
		//institutionMap.put("exhibitInstName", exhibitInstName);
		//institutionMap.put("exhibitInstTel", exhibitContact);
		if (resCntBoardDB != 0 && resCntBoardImgDB > 0 && resCntGenreDB > 0) { 
			// 먼저 select count(*)
			int resSelCnt = mapper.institutionSelectCount(exhibitInstName); // exhibitInstName의 institutionNo 갯 수 세기: 있으면 1, 없으면 0
			if (resSelCnt > 0) {
				institutionNo = jsonExhibitionService.jsonInstitutionSelect(exhibitInstName); // 전시기관 번호 반환; 
				resCntInstitutionDB = institutionNo;
				log.info("INSTITUTION 테이블에서 조회성공, institutionNo : {}", institutionNo);	
				
				resCntInstitutionDB = mapper.updateInstitutionDB(institutionNo);
				if (resCntInstitutionDB > 0) {
					log.info("INSTITUTION 테이블에서 수정성공, resCntInstitutionDB : {}", resCntInstitutionDB);
				} else {
					// 수정실패
					throw new GenreInsertException(); // 강제 예외 발생 시키는 구문 -> 이제 @Transactional에서 rollback한다.
				}				
				
			} else {
				resCntInstitutionDB = mapper.institutionInsert(institutionDB);
				if (resCntInstitutionDB > 0) {
					log.info("INSTITUTION 테이블에 새로 삽입 성공, resCntInstitutionDB : {}", resCntInstitutionDB);
					institutionNo = institutionDB.getInstitutionNo();
				} else {
					throw new InstitutionInsertException(); // 강제 예외 발생 시키는 구문 -> 이제 @Transactional에서 rollback한다.
				}					
				
			}
		}
		
		
		////////////////////////////////////////////////////// ExhibitionDB
		// 3) ExhibitionDB DTO에 맵핑하고, EXHIBITION DB에 insert 
		// DTO 값 할당
		ExhibitionDB exhibitionDB = new ExhibitionDB();
		exhibitionDB.setBoardNo(boardNo); 
		
		// ExhibitSubTitle의 글자수 200자 이상인 경우 slicing 필요 
		String lenExhibitSubTitle = exhibition.getExhibitSubTitle();
		String slicedExhibitSubTitle = null;
		if (lenExhibitSubTitle != null) {
			slicedExhibitSubTitle = JsonExhibitionController.truncateByByte(lenExhibitSubTitle, 200 - " ...... ".getBytes(StandardCharsets.UTF_8).length);
			slicedExhibitSubTitle += " ......";        			
		}
		exhibitionDB.setExhibitSubTitle(slicedExhibitSubTitle);
		
		exhibitionDB.setExhibitSite( exhibition.getExhibitLocation() );
		exhibitionDB.setExhibitDate( exhibition.getExhibitDate() );
		exhibitionDB.setExhibitContact( exhibition.getExhibitContact() );
		exhibitionDB.setExhibitAudience( exhibition.getExhibitAudience() );
		
		// exhibition.setExhibitCharge()의 경우:
		// 사용자로부터 숫자만("2000"문자열타입) 입력받을 것이므로 문자열을 숫자로 변환,
		// 그리고, "3000원", "무료", "관람료 없음" 같은 경우들 고려
        String strCharge = exhibition.getExhibitCharge();
        Pattern pattern = Pattern.compile("\\d+"); // 숫자 패턴
        Matcher matcher = pattern.matcher(strCharge);

        String parsedNumberString = null;
        int convCharge = 0;
        if (matcher.find()) { // 추출 숫자 맨 처음꺼 하나만
            parsedNumberString = matcher.group();
            convCharge = Integer.parseInt(parsedNumberString);
        } else {
        	if (strCharge.contains("무료") || strCharge.contains("없음") ) {
        		convCharge = 0;
        	} else {
        		convCharge = 7777777; // 예매불가, 전시관 직접문의 경우의 입장료 코드
                System.out.println("'" + strCharge + "'는 예매불가. 전시관에 직접문의 필요한 경우");
        	}
        }
        exhibitionDB.setExhibitCharge(convCharge);
		
        exhibitionDB.setGenreNo(genreNo);	
		exhibitionDB.setInstitutionNo(institutionNo); //  전시기관명을 전시기관번호(institutionNo)로 조회		

		// DB 서비스 호출		
		int resCntExhibitionDB = 0; // 0 실패, 1 성공
		if (resCntBoardDB != 0 && resCntBoardImgDB > 0 && resCntGenreDB > 0 && resCntInstitutionDB > 0) { 
			resCntExhibitionDB = mapper.updateExhibitionDB(exhibitionDB); 
			if (resCntExhibitionDB > 0) {
				log.info("EXHIBITION 테이블에서 수정 성공, resCntExhibitionDB : {}", resCntExhibitionDB);
			} else {
				// 수정 실패
				throw new ExhibitionInsertException(); // 강제 예외 발생 시키는 구문 -> 이제 @Transactional에서 rollback한다.
			}
		}
			
			
		////////////////////////////////////////////////////// AuthorDB
		// DTO 값 할당
		AuthorDB author = new AuthorDB(); 
		String authorsString = exhibition.getExhibitAuthor();
		String separator = "\\s*,\\s*";  // 쉼표 기준 분리 + 공백 제거
		int maxAuthors = 3;
		
		// authorsString == null인 경우 처리 ==> authorList에는 한 아이템 & setAuthorName(null)
		List<AuthorDB> authorList = new ArrayList<>();		
		if (authorsString != null) {
			authorList = JsonExhibitionController.parseAuthors(authorsString, separator, maxAuthors, boardNo);
		} else {
			author.setAuthorName(null);
			author.setBoardNo(boardNo);
			authorList.add(author);
		}
		
		// DB 서비스 호출       		
		int resCntAuthorDB = 0; // 0 실패, 1 성공
		if (resCntBoardDB != 0 && resCntBoardImgDB > 0 && resCntGenreDB > 0 && resCntInstitutionDB > 0
				&& resCntExhibitionDB > 0) { 
			resCntAuthorDB = mapper.updateAuthorDB(authorList); // result:0(삽입 실패)   
			if (resCntAuthorDB > 0) {
				log.info(" AUTHOR 테이블 update 성공, resCntAuthorDB: {}", resCntAuthorDB);		
			} else {
				throw new AuthorInsertException(); // 강제 예외 발생 시키는 구문 -> 이제 @Transactional에서 rollback한다.
			}
		}		
			

		////////////////////////////////////////////////////// ContributorDB	
		// DTO 값 할당
		ContributorDB contributor = new ContributorDB(); 
		String contributorString = exhibition.getExhibitContributor();       		
		
		contributor = JsonExhibitionController.parseContributor(contributorString, boardNo);
		
		// DB 서비스 호출        		
		
		int resCntContributorDB = 0; // 0 실패, 1 성공
		if (resCntBoardDB != 0 && resCntBoardImgDB > 0 && resCntGenreDB > 0 && resCntInstitutionDB > 0
				&& resCntExhibitionDB > 0 && resCntAuthorDB > 0
				) { 
			resCntContributorDB = mapper.updateContributorDB(contributor); // result:0(삽입 실패) 	
			if (resCntContributorDB > 0) {
				log.info(" CONTRIBUTOR 테이블 update 성공, resCntContributorDB: {}", resCntContributorDB);     						
			} else {
				// 수정 실패
				throw new ContributorInsertException(); // 강제 예외 발생 시키는 구문 -> 이제 @Transactional에서 rollback한다.
			}
		}			
		
		//return 0;
		return resCntBoardDB;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
