package edu.og.moa.board.exhibition.model.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.og.moa.board.exhibition.model.dao.JsonExhibitionMapper;
import edu.og.moa.board.exhibition.model.dto.AuthorDB;
import edu.og.moa.board.exhibition.model.dto.BoardDB;
import edu.og.moa.board.exhibition.model.dto.BoardImgDB;
import edu.og.moa.board.exhibition.model.dto.ContributorDB;
import edu.og.moa.board.exhibition.model.dto.ExhibitionDB;
import edu.og.moa.board.exhibition.model.dto.JsonBoardImage;
import edu.og.moa.board.exhibition.model.dto.LikeDB;
import edu.og.moa.common.utility.Util;

@Service
public class JsonExhibitionServiceImpl implements JsonExhibitionService {

	@Autowired
	private JsonExhibitionMapper mapper; 
	
	// json 데이터 BOARD 테이블에 insert
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int jsonBoardInsert(BoardDB board) throws IllegalStateException, IOException{
		// 제목만 XSS 방지처리:
		board.setBoardTitle(Util.XSSHandling(board.getBoardTitle() ) ); // title 변환시킨후 DB에 저장 ==> 타이틀을 읽어야하는 태그가 .html에 있기 때문

		//int boardNo = mapper.boardInsert(board); // 이거 성공해도 아직 commit하면 않됨 by @Transactional()		
		int result = mapper.jsonBoardInsert(board); // 이거 성공해도 아직 commit하면 않됨 by @Transactional()
		
		return result;
	}

	// json 데이터 BOARD 테이블에 insert using SelectKey in myBatis
	@Override
	public int jsonBoardInsertViaSelectKey(BoardDB board) throws IllegalStateException, IOException {
		// 제목만 XSS 방지처리:
		board.setBoardTitle(Util.XSSHandling(board.getBoardTitle() ) ); // title 변환시킨후 DB에 저장 ==> 타이틀을 읽어야하는 태그가 .html에 있기 때문
		
		//int boardNo = mapper.boardInsert(board); // 이거 성공해도 아직 commit하면 않됨 by @Transactional()		
		int boardNo = mapper.jsonBoardInsertViaSelectKey(board); // 이거 성공해도 아직 commit하면 않됨 by @Transactional()
		
		boardNo = board.getBoardNo();
		
		return boardNo;
	}
	
	// json 데이터 BOARD_IMG 테이블에 insert
	@Override
	public int jsonBoardImgInsert(List<BoardImgDB> boardImgList) throws IllegalStateException, IOException {
		int result = mapper.jsonBoardImgInsert(boardImgList); 
		return result;
	}

	// json 데이터 EXHIBITION 테이블에 insert하기위해, InstitutionNo를 INSTITUTION 테이블에서 조회
	@Override
	public int jsonInstitutionSelect(String exhibitInstName) throws IllegalStateException, IOException {
		int institutionNo = mapper.jsonInstitutionSelect(exhibitInstName); 
		return institutionNo;
	}

	// json 데이터 EXHIBITION 테이블에 insert하기위해, genreNo를 GENRE 테이블에서 조회
	@Override
	public int jsonGenreSelect(String genreName) throws IllegalStateException, IOException {
		int genreNo = mapper.jsonGenreSelect(genreName);
		return genreNo;
	}

	// json 데이터 EXHIBITION 테이블에 insert
	@Override
	public int jsonExhibitionInsert(ExhibitionDB exhibition) throws IllegalStateException, IOException {
		int result = mapper.jsonExhibitionInsert(exhibition);
		return result;
	}

	// json 데이터 AUTHOR 테이블에 insert
	@Override
	public int jsonAuthorInsert(List<AuthorDB> authorList) throws IllegalStateException, IOException {
		int result = mapper.jsonAuthorInsert(authorList); 
		return result;
	}

	
	// json 데이터 LIKE 테이블에 insert
	@Override
	public int jsonLikeInsert(List<LikeDB> likeList) throws IllegalStateException, IOException {
		int result = mapper.jsonLikeInsert(likeList);  // likeMemberNoList
		return result;
	}

	
	// json 데이터 CONTRIBUTOR 테이블에 insert
	@Override
	public int jsonContributorInsert(ContributorDB contributor) throws IllegalStateException, IOException {
		int result = mapper.jsonContributorInsert(contributor); 
		return result;
	}
	
	
//	// 게시글 작성
//	@Transactional(rollbackFor = Exception.class)
//	@Override
////	public int boardInsert(Board board, List<MultipartFile> images, String webPath, String filePath) throws IllegalStateException, IOException{
//	public int boardInsert(Board board, List<MultipartFile> images) throws IllegalStateException, IOException{
//		// 시퀀스는 mybatis가 한다.
//		
////		// 0. XSS 방지 처리: <script> 집어넣을 경우 무력화 ==> Thymeleaf에서는 필요없다(태그해석 할지말지 선택은 th:utext, [(...)]으로 지정가능)
////		board.setBoardTitle(Util.XSSHandling(board.getBoardTitle() ) ); // title 변환시킨후 DB에 저장
////		board.setBoardContent(Util.XSSHandling(board.getBoardContent() ) ); // content 변환 시킨후 DB에 저장
//
//		// 제목만 XSS 방지처리:
//		board.setBoardTitle(Util.XSSHandling(board.getBoardTitle() ) ); // title 변환시킨후 DB에 저장 ==> 타이틀을 읽어야하는 태그가 BoardDetail.html에 있기 때문
//		
//		// 1. (저번에 select한번에 3개의 select작업을 collection-tag이용해서 했다. 여기서는 그냥 따로 따로 insert하자.)
//		//    BOARD 테이블에 쓴글 INSERT(등록) 하기 (제목, 내용, 작성자, 게시판 코드)
//		//    (insert의 반환은 성공한 행의 갯수:0 또는 1)
//		//     		// 1. insert진행  -> 2. 게시글 번호 조회 (중간에 다른사용자의 다른 게시글 끼어들면 게시글 번호가 틀려지는 문제 -> 안쓰는 방법)
//		// 			// 1. 게시글 번호 조회 -> 2. insert진행 (요렇게 해야 문제 없다) ==>  => mapper.xml의 sql문에서 selectKey-tag이용하여 사용가능한 게시글 번호 조회하고, 그 번호로 insert 진행
//		// -> boardNo (시퀀스로 생성한 번호) 반환 받기
//		int boardNo = mapper.boardInsert(board); // 이거 성공해도 아직 commit하면 않됨 by @Transactional()
//		
//		// 실패시 서비스 종료 (밑에 코드 수행할 필요 없다
//		if (boardNo == 0) return 0;
//		
//		// mapper.xml에서 selectKey 태그로 인해 boardNo에 세팅된 값
//		// (useGeneratedKeys="true" 와 selectKey-tag를 이용하여 조회된 (insert용)게시글번호로, 이번호로 insert진행되었음; board가 얕은복사이기에 가능) 얻어오기
//		boardNo = board.getBoardNo();
//		
//		
//		// 2. 게시글 삽입 성공 시
//		//    업로드된 이미지가 있다면 BOARD_IMG 테이블에 삽입하는 DAO호출
//		if (boardNo != 0) {
//			// List<MultipartFile> images (5개)
//			// -> 업로드된 파일이 담긴 객체
//			// -> 단, 업로드된 파일이 없어도 MultipartFile 객체는 존재(5개, 파일 없을때는 size=0 또는 파일명(getOriginalFileName())이 "")
//			
//			// 실제로 업로드된 파일의 정보를 기록할 List
//			List<BoardImage> uploadList = new ArrayList<BoardImage>();
//			
//			// images에 담겨있는 파일 중 실제로 업로드된 파일만 분류 
//			for(int i=0; i<images.size(); i++) { // 이미지 파일 있으나 없으나, images.size()=5가 기본
//				
//				// i번째 요소에 업로드한 파일이 있다면
//				if (images.get(i).getSize() > 0) { // 업로드한 이미지 있다.
//					// img에 파일 정보를 담아서 uploadList에 추가
//					BoardImage img = new BoardImage();
//					
//					img.setImagePath(webPath); // 웹 접근 경로
//					
//					// 파일 원본명
//					String fileName = images.get(i).getOriginalFilename(); // 파일 원본명 from 리스트
//					
//					// 파일 변경명 img에 세팅
//					img.setImageReName(Util.fileRename(fileName));
//					
//					// 파일 원본명 img에 세팅
//					img.setImageOriginal(fileName);
//					
//					// 다른 필요한 값들 img에 세팅
//					img.setImageOrder(i); 	 // 이미지 순서
//					img.setBoardNo(boardNo); // 게시글 번호
//					
//					uploadList.add(img);
//					
//				}
//				
//			} // 분류 for문 종료 -> for문 각 i마다 DAO호출 않고, 한번에 images.size()을 한번 sql문으로 얻어오기
//			
//			// 분류 작업 후 uploadList가 비어있지 않은 경우
//			// == 업로드한 파일이 존재
//			if(!uploadList.isEmpty()) {
//				
//				// BOARD_IMG 테이블에 insert 하기
//				int result = mapper.insertImageList(uploadList); // 이것까지 성공해야 commit by @Transactional()
//				// result == 성공한 행의 개수
//				// uploadList.size() = 5   
//				// 위 둘이 다르면 -> 부분 성공, 같으면 -> 전체 실패
//				//
//				// 삽입된 행의 갯수(result)와 uploadList의 개수(uploadList.size())가 같다면
//				// == 전체 insert 성공
//				if (result == uploadList.size()) { // 전체 성공 or 부분 성공/전체 실패
//					
//					// 서버에 파일 저장(transferTo())
//					// cf) myPageServiceImpl.java에서: if(rename != null) profileImage.transferTo(new File(filePath + rename));
//					//     profileImage -> MultipartFile profileImage (MultipartFile 타입)
//					//
//					// images: 실제 파일이 담긴 객체 리스트 (List<MultipartFile> images ) -> tranferTo()는 여기서...
//					// (업로드 안된 인덱스는 빈칸으로)
//					//
//					// uploadList : 업로드된 파일의 정보 리스트 (List<BoardImage> uploadList)
//					// (원본명, 변경명, 이미지 순서, 경로, 게시글 번호)
//					//
//					// images 업로드된 인덱스 == uploadList의 순서
//					// ex) images 5개 -> uploadList 3개 ->그러면 for문은 3번만 돌며 upload해주면 된다.
//					
//					for (int i=0; i<uploadList.size(); i++) {
//						// upload된 index번호의 이미지만 upload할것이므로, 그에 대응하는 images의 이미지 얻어와야 함
//						//
//						// 이미지 순서
//						int index = uploadList.get(i).getImageOrder(); //
//						
//						// 변경명
//						String rename = uploadList.get(i).getImageReName();
//						//images.get(index).transferTo(new File(filePath + 변경명)) throws IllegalStateException, IOException; 
//						images.get(index).transferTo(new File(filePath + rename));  // index에 해당하는 images[index]만 서버로 옮겨준다(서버에 저장한다)
//						
//						
//					}
//					
//					
//					
//				} else { // 일부 또는 전체 insert 실패
//					// * 웹 서비스 수행 중 1개라도 실패하면 전체 실패 *
//					// -> rollback 필요 (but, @Transactional rollback은 exception이 발생해야만 rollback진행
//					//
//					// @Transactional (rollbackFor = Exception.class)
//					// -> 예외가 발생해야만 롤백한다.
//					
//					// 따라서 예외를 강제로 발생시켜서 rollback 해야 한다.
//					// -> 사용자 정의 예외 (강제)생성 by "throw"
//					throw new FileUploadException(); // 강제 예외 발생 시키는 구문 -> 이제 @Transactional에서 rollback한다.
//					
//					
//					
//					
//				}
//				
//			}
//			
//		}
//
//		//return 0;
//		return boardNo;
//	}


    @Override
    public List<JsonBoardImage> selectExhibitionThumbnailList() {
        return mapper.selectExhibitionThumbnailList();
    }
	
	

}
