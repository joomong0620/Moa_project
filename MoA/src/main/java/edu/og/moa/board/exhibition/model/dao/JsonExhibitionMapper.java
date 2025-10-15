package edu.og.moa.board.exhibition.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import edu.og.moa.board.exhibition.model.dto.AuthorDB;
import edu.og.moa.board.exhibition.model.dto.BoardDB;
import edu.og.moa.board.exhibition.model.dto.BoardImgDB;
import edu.og.moa.board.exhibition.model.dto.ContributorDB;
import edu.og.moa.board.exhibition.model.dto.ExhibitionDB;
import edu.og.moa.board.exhibition.model.dto.JsonBoardImage;
import edu.og.moa.board.exhibition.model.dto.LikeDB;


@Mapper
public interface JsonExhibitionMapper {

	// json 데이터 BOARD 테이블에 insert
	public int jsonBoardInsert(BoardDB board); // 로직은 JsonExhibitionServiceImpl.java로  

	// json 데이터 BOARD 테이블에 insert using SelectKey in myBatis
	public int jsonBoardInsertViaSelectKey(BoardDB board);
	
	// json 데이터 BOARD_IMG 테이블에 insert
	public int jsonBoardImgInsert(List<BoardImgDB> boardImgList);

	// json 데이터 EXHIBITION 테이블에 insert하기위해, InstitutionNo를 INSTITUTION 테이블에서 조회
	public int jsonInstitutionSelect(String exhibitInstName);

	// json 데이터 EXHIBITION 테이블에 insert하기위해, genreNo를 GENRE 테이블에서 조회
	public int jsonGenreSelect(String genreName);

	// json 데이터 EXHIBITION 테이블에 insert
	public int jsonExhibitionInsert(ExhibitionDB exhibition);

	// json 데이터 AUTHOR 테이블에 insert
	public int jsonAuthorInsert(List<AuthorDB> authorList);

	// json 데이터 LIKE 테이블에 insert
	public int jsonLikeInsert(List<LikeDB> likeList); // likeMemberNoList

	// json 데이터 CONTRIBUTOR 테이블에 insert
	public int jsonContributorInsert(ContributorDB contributor);

	// 메인화면 점시 썸네일 조회(KSY)
	public List<JsonBoardImage> selectExhibitionThumbnailList();




}
