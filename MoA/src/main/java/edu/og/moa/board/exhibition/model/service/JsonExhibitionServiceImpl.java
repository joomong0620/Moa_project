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

		int result = mapper.jsonBoardInsert(board); // 이거 성공해도 아직 commit하면 않됨 by @Transactional()
		
		return result;
	}

	// json 데이터 BOARD 테이블에 insert using SelectKey in myBatis
	@Override
	public int jsonBoardInsertViaSelectKey(BoardDB board) throws IllegalStateException, IOException {
		// 제목만 XSS 방지처리:
		board.setBoardTitle(Util.XSSHandling(board.getBoardTitle() ) ); 
		
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
	


    @Override
    public List<JsonBoardImage> selectExhibitionThumbnailList() {
        return mapper.selectExhibitionThumbnailList();
    }
	
	
}
