package edu.og.moa.board.freeboard.model.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.og.moa.board.freeboard.model.dao.FreeBoardMapper2;
import edu.og.moa.board.freeboard.model.dto.Board;
import edu.og.moa.board.freeboard.model.dto.BoardImage;
import edu.og.moa.board.freeboard.model.exception.imageDeleteException;
import edu.og.moa.common.utility.Util;


@Service
@PropertySource("classpath:/config.properties")
public class FreeBoardServiceImpl2 implements FreeBoardService2 {

	@Value("${my.freeboard.webpath}")
	private String webPath;

	// 추가: 파일 저장 경로 주입
	@Value("${my.freeboard.location}")
	private String filePath;

	@Autowired
	private FreeBoardMapper2 mapper;

	// 게시글 작성
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int FreeboardInsert(Board board, List<MultipartFile> images) throws IllegalStateException, IOException {

		// XSS 방지
		board.setBoardTitle(Util.XSSHandling(board.getBoardTitle()));
		board.setBoardContent(Util.XSSHandling(board.getBoardContent()));

		// 게시글 삽입
		int boardNo = mapper.FreeboardInsert(board);

		// 실패 시 서비스 종료
		if (boardNo == 0)
			return 0;

		boardNo = board.getBoardNo();

		// 2. 게시글 삽입 성공 시
		// 업로드된 이미지가 있다면 BOARD_IMG 테이블에 삽입하는 DAO 호출
		if (boardNo != 0) {
			// List<MultipartFile> images
			// -> 업로드된 파일이 담긴 객체
			// -> 단, 업로드된 파일이 없어도 MultipartFile 객체는 존재(5개)

			// 실제로 업로드된 파일의 정보를 기록할 List
			List<BoardImage> uploadList = new ArrayList<BoardImage>();

			// images에 담겨 있는 파일 중 실제로 업로드된 파일만 분류
			for (int i = 0; i < images.size(); i++) {

				// i번째 요소에 업로드한 파일이 있다면
				if (images.get(i).getSize() > 0) {

					// img에 파일 정보를 담아서 uploadList에 추가
					BoardImage img = new BoardImage();

					img.setImgPath(webPath); // 웹 접근 경로

					// 파일 원본명 얻어오기
					String fileName = images.get(i).getOriginalFilename();

					img.setImgRename(Util.fileRename(fileName)); // 파일 변경명 세팅

					img.setImgOrig(fileName); // 파일 원본명 세팅

					img.setImgOrder(i); // 이미지 순서
					img.setBoardNo(boardNo); // 게시글 번호

					uploadList.add(img);
				}

			} // 분류 for문 종료

			// DB에 이미지 정보 저장
			if (!uploadList.isEmpty()) {
				int result = mapper.insertImageList(uploadList);

				if (result != uploadList.size()) {
					throw new RuntimeException("이미지 DB 삽입 실패");
				}

				// 변경: folderPath 사용
				File folder = new File(filePath);
				if (!folder.exists())
					folder.mkdirs();

				for (BoardImage img : uploadList) {
					MultipartFile mf = images.get(img.getImgOrder());
					File dest = new File(filePath + img.getImgRename());
					mf.transferTo(dest);
				}
			}
		}

		return boardNo;
	}

	// 게시글 수정
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int FreeboardUpdate(Board board, List<MultipartFile> images, String deleteList)
			throws IllegalStateException, IOException {

		// 0. XSS 방지 처리
		board.setBoardTitle(Util.XSSHandling(board.getBoardTitle()));
		board.setBoardContent(Util.XSSHandling(board.getBoardContent()));

		// 1. 게시글 제목/내용만 수정
		int rowCount = mapper.FreeboardUpdate(board);

		// 2. 게시글 수정 성공 시
		if (rowCount > 0) {

			// 3. 삭제할 이미지가 존재한다면
			if (deleteList != null && !deleteList.trim().equals("")) {

			    // "1,2,3" -> ["1", "2", "3"] 변환
			    List<String> deleteListArr = new ArrayList<>();
			    for (String s : deleteList.split(",")) {
			        deleteListArr.add(s.trim());
			    }

			    Map<String, Object> deleteMap = new HashMap<>();
			    deleteMap.put("boardNo", board.getBoardNo());
			    deleteMap.put("deleteList", deleteListArr); // ✅ 리스트로 변환해서 전달

			    int count = mapper.FreecheckImage(deleteMap);

				if (count > 0) {

					// 3-2) deleteList에 작성된 이미지 모두 삭제
					rowCount = mapper.FreeimageDelete(deleteMap);

					// 삭제 실패 시 전체 롤백 -> 예외 강제로 발생시키기
					if (rowCount == 0)
						throw new imageDeleteException();
				}

			}

			// 4. 새로 업로드된 이미지 분류 작업

			// 실제로 업로드된 파일의 정보를 기록할 List
			List<BoardImage> uploadList = new ArrayList<BoardImage>();

			// images에 담겨 있는 파일 중 실제로 업로드된 파일만 분류
			for (int i = 0; i < images.size(); i++) {

				// i번째 요소에 업로드한 파일이 있다면
				if (images.get(i).getSize() > 0) {

					// img에 파일 정보를 담아서 uploadList에 추가
					BoardImage img = new BoardImage();

					img.setImgPath(webPath); // 웹 접근 경로

					// 파일 원본명 얻어오기
					String fileName = images.get(i).getOriginalFilename();

					img.setImgRename(Util.fileRename(fileName)); // 파일 변경명 세팅
					img.setImgOrig(deleteList); // 파일 원본명 세팅

					img.setImgOrder(i); // 이미지 순서
					img.setBoardNo(board.getBoardNo()); // 게시글 번호

					uploadList.add(img); // uploadList에 추가

					// 오라클은 다중 UPDATE를 지원하지 않기 때문에
					// 하나씩 UPDATE 수행
					rowCount = mapper.imageUpdate(img);

					// UPDATE 실패 시 : DB에 이미지가 없는 경우 -> 이미지 삽입 진행
					if (rowCount == 0)
						rowCount = mapper.imageInsert(img);

				}

			} // 분류 for문 종료

			// 5. uploadList에 있는 이미지들만 서버에 저장
			if (!uploadList.isEmpty()) {

				// 서버에 파일 저장(transferTo())
				for (int i = 0; i < uploadList.size(); i++) {
					int index = uploadList.get(i).getImgOrder();

					String rename = uploadList.get(i).getImgRename();

					images.get(index).transferTo(new File(filePath + rename));

				}
			}
		} // 게시글 수정 성공 if문 끝

		return rowCount;
	}

	// 게시글 삭제
	@Override
	public int FreeboardDelete(int boardNo) {
		return mapper.FreeboardDelete(boardNo);
	}

}