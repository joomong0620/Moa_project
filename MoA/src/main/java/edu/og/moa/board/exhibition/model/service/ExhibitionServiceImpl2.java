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
@PropertySource("classpath:/config.properties") 
public class ExhibitionServiceImpl2 implements ExhibitionService2{

	@Value("${pyy.exhibition.webpath}") 
	private String webPath;
	
	@Value("${pyy.exhibition.location}")  
	private String filePath;
	
	@Autowired
	private ExhibitionMapper2 mapper; 	
	
	@Autowired
	private JsonExhibitionService jsonExhibitionService;
	
	// 게시글 삭제
	@Override
	public int exhibitionDelete(int exhibitNo) {
		return mapper.exhibitionDelete(exhibitNo);
	}


	// 게시글 삽입
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int exhibtionInsert(Exhibition exhibition, List<MultipartFile> images)
			throws IllegalStateException, IOException {
		//  exhibition에 담긴 정보 각 DB에 들어갈 정보로 각 DB별 그룹핑하여 json-to-DB 로딩경우처럼 순차로 진행, error경우는 롤백
		// BoardDB, BoardImgDB, ExhibitionDB, AuthorDB, LikeDB, Contributor (+ GenreDB, InstitutionDB)
		
		////////////////////////////////////// BoardDB
		// BoardDB DTO에 맵핑하고, BOAR DB에 insert 
		BoardDB board = new BoardDB();
		
		// 0. XSS 방지 처리: 사용자가 <script> 집어넣을 경우 무력화 
		// 제목만 XSS 방지처리:
		board.setBoardTitle(Util.XSSHandling(exhibition.getExhibitTitle())); 
		
		// ExhibitContent의 글자수 > 4000 이상인 경우 slicing 필요
		String lenExhibitContent = exhibition.getExhibitContent();
		String slicedExhibitContent = null;
		if (lenExhibitContent != null) {
			slicedExhibitContent = JsonExhibitionController.truncateByByte(lenExhibitContent, 4000 - " ...... ".getBytes(StandardCharsets.UTF_8).length);
			slicedExhibitContent += " ......";
		}        		
		
		
		board.setBoardContent( slicedExhibitContent );
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date curTime = new Date();
		String currentTime = sdf.format(curTime);
		log.info("currentTime format check (2025-06-17 00:10:02 ?): {}", currentTime);
		board.setBCreateDate(null);// 날짜 형식 "2025-06-17 00:10:02"
		board.setBUpdateDate(null);
		
		board.setBoardCount(0); 		// 처음 입력시에는 exhibition.getReadCount() = 0   
		board.setMemberNo(exhibition.getMemberNo()); 			// 게시글 입력 회원의 회원번호
		board.setCommunityCode(exhibition.getCommunityCode()); 	// 3번 전시게시판 값 할당
		
		// DB 서비스 호출	
		// SelectKey를 이용하여 가용한 시퀀스값 먼저 조회하여, 그것을 boardNo값으로 입력 
		int boardNo = jsonExhibitionService.jsonBoardInsertViaSelectKey(board); // 0 실패, 1성공
			
		
		if (boardNo == 0) {
			return 0; // 실패시 서비스 종료 
		} else {
			log.info("BOARD 테이블에서 삽입 성공, boardNo : {}", boardNo);

		}
			
		boardNo = board.getBoardNo();
		
		////////////////////////////////////// BoardImgDB
		// BoardImgDB DTO에 맵핑하고, BOARD_IMG DB에 insert 
		// 2. 게시글 삽입 성공 시: 업로드된 이미지 BOARD_IMG 테이블에 삽입
		int resultBoardImgDB = 0; // 0 실패, 1 성공
		if (boardNo != 0) {

			// 실제로 업로드된 파일의 정보를 기록할 List
			List<BoardImgDB> uploadList = new ArrayList<BoardImgDB>();
			
			// 실제로 업로드된 파일
			for(int i=0; i<images.size(); i++) { 
				
				// i번째 요소에 업로드한 파일이 있다면
				if (images.get(i).getSize() > 0) { 
					// img에 파일 정보를 담아서 uploadList에 추가
					BoardImgDB boardImg = new BoardImgDB();
					
					boardImg.setImgPath(webPath); // 웹 접근 경로
					
					// 파일 원본명
					String fileName = images.get(i).getOriginalFilename(); 
					
					// 파일 변경명 img에 세팅
					boardImg.setImgRename(Util.fileRename(fileName));
					
					// 파일 원본명 img에 세팅
					boardImg.setImgOrig(fileName);
					
					// 다른 필요한 값들 img에 세팅
					boardImg.setImgOrder(i); 	 // 이미지 순서
					boardImg.setBoardNo(boardNo); // 게시글 번호
					
					uploadList.add(boardImg);
					
				}
				
			} 
			
			// 분류 작업 후 uploadList가 비어있지 않은 경우
			// == 업로드한 파일이 존재
			if(!uploadList.isEmpty()) {
				
				// BOARD_IMG 테이블에 insert 
				resultBoardImgDB = jsonExhibitionService.jsonBoardImgInsert(uploadList); // 이것까지 성공해야 commit by @Transactional() => 0 실패, 1 성공
				
				// 삽입된 행의 갯수(result)와 uploadList의 개수(uploadList.size())가 같다면 전체 insert 성공
				if (resultBoardImgDB == uploadList.size()) { 
					
					for (int i=0; i<uploadList.size(); i++) {
						// 이미지 순서
						int index = uploadList.get(i).getImgOrder(); //
						
						// 변경명
						String rename = uploadList.get(i).getImgRename();
						images.get(index).transferTo(new File(filePath + rename));  // 전시의 경우:one & only
					}
					
					log.info("BOARD_IMG 테이블에서 삽입(&파일저장) 성공, resultBoardImgDB : {}", resultBoardImgDB);
					
				} else { 
					// * 웹 서비스 수행 중 1개라도 실패하면 전체 실패 *
					// 예외를 강제로 발생시켜서 rollback by 사용자 정의 예외 
					throw new FileUploadException(); 
						
				}
					
			}			
			
		}
		
		
		////////////////////////////////////////////////////// GenreDB 
		GenreDB genreDB = new GenreDB();
		
		String genreName = exhibition.getExhibitGenre();
		genreDB.setGenreName(genreName); // genreNo는 아래 insert generated Key값으로 
		
		int resultGenreDB = 0; // 0 실패, 1 성공
		int genreNo = 0;
		if (boardNo != 0 && resultBoardImgDB > 0) { 
			
			// 먼저 select count(*)
			int resSelCnt = mapper.genreSelectCount(genreName); 
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
					throw new GenreInsertException(); // 강제 예외 발생
				}
			}
			
		}			
		
		
		////////////////////////////////////////////////////// InstitutionDB 
		InstitutionDB institutionDB = new InstitutionDB();

		String exhibitInstName = exhibition.getExhibitInstitution(); 
		String exhibitContact = exhibition.getExhibitContact();
		institutionDB.setExhibitInstName(exhibitInstName);
		institutionDB.setExhibitInstTel(exhibitContact);
		int resultInstitutionDB = 0; // 0 실패, 1 성공
		int institutionNo = 0;

		if (boardNo != 0 && resultBoardImgDB > 0 && resultGenreDB > 0) { 
			// 먼저 select count(*)
			int resSelCnt = mapper.institutionSelectCount(exhibitInstName); // exhibitInstName의 institutionNo 갯 수: 있으면 1, 없으면 0
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
				throw new ExhibitionInsertException(); // 강제 예외 발생 
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
				throw new AuthorInsertException(); // 강제 예외 발생 
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
				throw new ContributorInsertException(); // 강제 예외 발생 
			}
		}			
		
		return boardNo;
	}


	// 게시글 수정
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int exhibitionUpdate(Exhibition exhibition, List<MultipartFile> images, String deleteList) throws IllegalStateException, IOException {
		// exhibition에 담긴 정보를 각 DB에 들어갈 정보로 나누어 DB 하나씩 순차처리
		// BoardDB, BoardImgDB, ExhibitionDB, AuthorDB, LikeDB, Contributor (+ GenreDB, InstitutionDB)
		
		////////////////////////////////////// BoardDB
		// BoardDB DTO에 맵핑하고, BOARD DB update 
		BoardDB board = new BoardDB();
		
		board.setBoardNo(exhibition.getExhibitNo()); // boardNo === exhibitNo
		// 0. XSS 방지 처리: <script> 
		// 제목만 XSS 방지처리:
		board.setBoardTitle(Util.XSSHandling(exhibition.getExhibitTitle())); 
		
		// ExhibitContent의 글자수 > 4000 이상인 경우 slicing 필요
		String lenExhibitContent = exhibition.getExhibitContent();
		String slicedExhibitContent = null;
		if (lenExhibitContent != null) {
			slicedExhibitContent = JsonExhibitionController.truncateByByte(lenExhibitContent, 4000 - " ...... ".getBytes(StandardCharsets.UTF_8).length);
			slicedExhibitContent += " ......";
		}        		
		
		
		board.setBoardContent( slicedExhibitContent );
		board.setBCreateDate(exhibition.getExhibitCreateDate());
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date updTime = new Date();
		String updateTime = sdf.format(updTime);
		log.info("currentTime format check (2025-06-17 00:10:02 ?): {}", updateTime); // 2025-10-17 15:55:26

		board.setBUpdateDate(updateTime); // 업데이트 시간은 표시해 줘야함
		
		// DB 서비스 호출	
		int resCntBoardDB = mapper.updateBoardDB(board); // 0 실패, 1성공	
		
		if (resCntBoardDB == 0) {
			return 0; // 실패시 서비스 종료 
		} else {
			log.info("BOARD 테이블 수정 성공, resCntBoardDB : {}", resCntBoardDB);

		}
			
		int boardNo = board.getBoardNo(); 
		
		////////////////////////////////////// BoardImgDB
		// BoardImgDB DTO에 맵핑하고, BOARD_IMG DB update 
		int resCntBoardImgDB = 0; // 0 실패, 1 성공
		if (resCntBoardDB != 0) {
			
			// 실제로 업로드된 파일의 정보를 기록할 List
			List<BoardImgDB> uploadList = new ArrayList<BoardImgDB>();
			
			// images에 담겨있는 파일 중 실제로 업로드된 파일만 
			for(int i=0; i<images.size(); i++) { 
				
				// i번째 요소에 업로드한 파일이 있다면
				if (images.get(i).getSize() > 0) { 
					// img에 파일 정보를 담아서 uploadList에 추가
					BoardImgDB boardImg = new BoardImgDB();
					
					boardImg.setBoardNo(boardNo);
					boardImg.setImgPath(webPath); 
					
					// 파일 원본명
					String fileName = images.get(i).getOriginalFilename(); 
					
					// 파일 변경명 img에 세팅
					boardImg.setImgRename(Util.fileRename(fileName));
					
					// 파일 원본명 img에 세팅
					boardImg.setImgOrig(fileName);
					
					// 다른 필요한 값들 img에 세팅
					boardImg.setImgOrder(i); 	 // 이미지 순서
					boardImg.setBoardNo(boardNo); // 게시글 번호
					
					uploadList.add(boardImg);
					
				}
				
			} 
			
			// 분류 작업 후 uploadList가 비어있지 않은 경우
			// == 업로드한 파일이 존재
			if(!uploadList.isEmpty()) {
				
				// BOARD_IMG 테이블에 insert 하기
				resCntBoardImgDB = mapper.updateBoardImgDBList(uploadList); // 이것까지 성공해야 commit 
				
				// 삽입된 행의 갯수(result)와 uploadList의 개수(uploadList.size())가 같다면 전체 insert 성공
				if (resCntBoardImgDB == uploadList.size()) { 
					
					for (int i=0; i<uploadList.size(); i++) {

						// 이미지 순서
						int index = uploadList.get(i).getImgOrder(); 
						
						// 변경명
						String rename = uploadList.get(i).getImgRename();
						images.get(index).transferTo(new File(filePath + rename));  
					}
					
					log.info("BOARD_IMG 테이블에서 수정(&파일저장) 성공, resCntBoardImgDB : {}", resCntBoardImgDB);
					
				} else { 
					// * 웹 서비스 수행 중 1개라도 실패하면 전체 실패 *
					throw new FileUploadException(); // 강제 예외 발생 
						
				}
					
			}			
			
		}
		
		
		////////////////////////////////////////////////////// GenreDB 
		GenreDB genreDB = new GenreDB();
		
		String genreName = exhibition.getExhibitGenre();
		genreDB.setGenreName(genreName); 
		
		int resCntGenreDB = 0; 
		int genreNo = 0;
		if (resCntBoardDB != 0 && resCntBoardImgDB > 0) { 
			
			// 먼저 select count(*)
			int resSelCnt = mapper.genreSelectCount(genreName); // genreName의 genreNo 수 세기: 있으면 1, 없으면 0
			if (resSelCnt > 0) {
				genreNo = jsonExhibitionService.jsonGenreSelect(genreName); // 장르 넘버 반환; genreNo는  GenreDB 필드명 
				resCntGenreDB = genreNo;
				log.info("GENRE 테이블에서 조회성공, genreNo : {}", genreNo);
				
				resCntGenreDB = mapper.updateGenreDB(genreNo);
				if (resCntGenreDB > 0) {
					log.info("GENRE 테이블에서 수정성공, resCntGenreDB : {}", resCntGenreDB);
				} else {
					// 수정실패
					throw new GenreInsertException(); // 강제 예외 발생 
				}
			} else {
				resCntGenreDB = mapper.genreInsert(genreDB);
				if (resCntGenreDB > 0) {
					log.info("GENRE 테이블에 새로 삽입성공, resCntGenreDB : {}", resCntGenreDB);
					genreNo = genreDB.getGenreNo();
				} else {
					// 새장르번호 삽입실패
					throw new GenreInsertException(); // 강제 예외 발생 
				}
			}
			
		}			
		
		
		////////////////////////////////////////////////////// InstitutionDB 
		InstitutionDB institutionDB = new InstitutionDB();
		String exhibitInstName = exhibition.getExhibitInstitution();  
		String exhibitContact = exhibition.getExhibitContact();
		institutionDB.setExhibitInstName(exhibitInstName);
		institutionDB.setExhibitInstTel(exhibitContact);
		int resCntInstitutionDB = 0; // 0 실패, 1 성공
		int institutionNo = 0;

		if (resCntBoardDB != 0 && resCntBoardImgDB > 0 && resCntGenreDB > 0) { 
			// 먼저 select count(*)
			int resSelCnt = mapper.institutionSelectCount(exhibitInstName);
			if (resSelCnt > 0) {
				institutionNo = jsonExhibitionService.jsonInstitutionSelect(exhibitInstName); 
				resCntInstitutionDB = institutionNo;
				log.info("INSTITUTION 테이블에서 조회성공, institutionNo : {}", institutionNo);	
				
				resCntInstitutionDB = mapper.updateInstitutionDB(institutionNo);
				if (resCntInstitutionDB > 0) {
					log.info("INSTITUTION 테이블에서 수정성공, resCntInstitutionDB : {}", resCntInstitutionDB);
				} else {
					// 수정실패
					throw new GenreInsertException(); // 강제 예외 발생 
				}				
				
			} else {
				resCntInstitutionDB = mapper.institutionInsert(institutionDB);
				if (resCntInstitutionDB > 0) {
					log.info("INSTITUTION 테이블에 새로 삽입 성공, resCntInstitutionDB : {}", resCntInstitutionDB);
					institutionNo = institutionDB.getInstitutionNo();
				} else {
					throw new InstitutionInsertException(); // 강제 예외 발생 
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
				throw new ExhibitionInsertException(); // 강제 예외 발생 
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
			
			int cntUpdateAuthorDB = 0;
			for (AuthorDB oneAuthor : authorList) {
				cntUpdateAuthorDB += mapper.updateAuthorDB(oneAuthor);  // 
			}
			resCntAuthorDB = cntUpdateAuthorDB;
			
			
			if (resCntAuthorDB > 0) {
				log.info(" AUTHOR 테이블 update 성공, resCntAuthorDB: {}", resCntAuthorDB);		
			} else {
				throw new AuthorInsertException(); // 강제 예외 발생 
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
				throw new ContributorInsertException(); // 강제 예외 발생 
			}
		}			

		return resCntBoardDB; // 업데이트 성공 행의 갯수
	}
	
	
}
