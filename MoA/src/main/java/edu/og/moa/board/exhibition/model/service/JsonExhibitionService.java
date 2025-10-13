package edu.og.moa.board.exhibition.model.service;

import java.io.IOException;
import java.util.List;

import edu.og.moa.board.exhibition.model.dto.AuthorDB;
import edu.og.moa.board.exhibition.model.dto.BoardDB;
import edu.og.moa.board.exhibition.model.dto.BoardImgDB;
import edu.og.moa.board.exhibition.model.dto.ContributorDB;
import edu.og.moa.board.exhibition.model.dto.ExhibitionDB;
import edu.og.moa.board.exhibition.model.dto.LikeDB;

public interface JsonExhibitionService {


	/** json 데이터 BOARD 테이블에 insert
	 * @param board
	 * @return 삽입 성공한 행의 갯수
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	int jsonBoardInsert(BoardDB board) throws IllegalStateException, IOException;

	
	/** json 데이터 BOARD 테이블에 insert using SelectKey in myBatis
	 * @param board
	 * @return 삽입 성공한 행의 boardNo
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	int jsonBoardInsertViaSelectKey(BoardDB board) throws IllegalStateException, IOException;

	
	
	
	/** json 데이터 BOARD_IMG 테이블에 insert
	 * @param boardImgList
	 * @return 성공한 행의 갯수
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	int jsonBoardImgInsert(List<BoardImgDB> boardImgList) throws IllegalStateException, IOException;


	/** json 데이터 EXHIBITION 테이블에 insert하기위해, InstitutionNo를 INSTITUTION 테이블에서 조회
	 * @param exhibitInstName
	 * @return 조회된 InstitutionNo 
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	int jsonInstitutionSelect(String exhibitInstName) throws IllegalStateException, IOException;


	/** json 데이터 EXHIBITION 테이블에 insert하기위해, genreNo를 GENRE 테이블에서 조회
	 * @param genreName
	 * @return 조회된 genreNo
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	int jsonGenreSelect(String genreName) throws IllegalStateException, IOException;


	/** json 데이터 EXHIBITION 테이블에 insert
	 * @param exhibition
	 * @return 성공한 행의 갯수
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	int jsonExhibitionInsert(ExhibitionDB exhibition) throws IllegalStateException, IOException;


	/** json 데이터 AUTHOR 테이블에 insert
	 * @param authorList
	 * @return 성공한 행의 갯수
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	int jsonAuthorInsert(List<AuthorDB> authorList) throws IllegalStateException, IOException;


	/** json 데이터 LIKE 테이블에 insert
	 * @param memberNoList
	 * @return 성공한 행의 갯수 (likeCount 수대로)
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	int jsonLikeInsert(List<LikeDB> memberNoList) throws IllegalStateException, IOException;


	/** json 데이터 CONTRIBUTOR 테이블에 insert
	 * @param contributor
	 * @return 성공한 행의 갯수 
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	int jsonContributorInsert(ContributorDB contributor) throws IllegalStateException, IOException;



	


}
