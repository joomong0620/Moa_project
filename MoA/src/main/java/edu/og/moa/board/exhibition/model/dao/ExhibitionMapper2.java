package edu.og.moa.board.exhibition.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import edu.og.moa.board.exhibition.model.dto.AuthorDB;
import edu.og.moa.board.exhibition.model.dto.BoardDB;
import edu.og.moa.board.exhibition.model.dto.BoardImgDB;
import edu.og.moa.board.exhibition.model.dto.ContributorDB;
import edu.og.moa.board.exhibition.model.dto.ExhibitionDB;
import edu.og.moa.board.exhibition.model.dto.GenreDB;
import edu.og.moa.board.exhibition.model.dto.InstitutionDB;

@Mapper
public interface ExhibitionMapper2 {
	
	/** 게시글 삭제
	 * @param exhibitionNo
	 * @return result (0: 실패, 1:성공)
	 */
	public int exhibitionDelete(int exhibitionNo);

	



	/** 장르 삽입
	 * @param genreDB
	 * @return result (0: 실패, 1:성공)
	 */
	public int genreInsert(GenreDB genreDB);





	/** 전시기관 삽입
	 * @param institutionDB
	 * @return result (0: 실패, 1:성공)
	 */
	public int institutionInsert(InstitutionDB institutionDB);





	/** genreName의 genreNo 갯 수 세기
	 * @param genreName
	 * @return result ( 있으면 1, 없으면 0)
	 */
	public int genreSelectCount(String genreName);





	/** exhibitInstName의 institutionNo 갯 수 세기
	 * @param exhibitInstName
	 * @return result ( 있으면 1, 없으면 0)
	 */
	public int institutionSelectCount(String exhibitInstName);





	/** BOARD(BoardDB) 수정
	 * @param board
	 * @return result (성공한 행의 갯수: 있으면 1, 없으면 0)
	 */
	public int updateBoardDB(BoardDB board);





	/** BOARD_IMG(BoardImgDB) 수정
	 * @param uploadList
	 * @return result (성공한 행의 갯수: 있으면 1, 없으면 0)
	 */
	public int updateBoardImgDB(List<BoardImgDB> uploadList);





	/** GENRE(genreDB) 수정
	 * @param genreNo
	 * @return result (성공한 행의 갯수: 있으면 1, 없으면 0)
	 */
	public int updateGenreDB(int genreNo);





	/** INSTITUTION(institutionDB) 수정
	 * @param institutionNo
	 * @return result (성공한 행의 갯수: 있으면 1, 없으면 0)
	 */
	public int updateInstitutionDB(int institutionNo);





	/** EXHIBITION(ExhibitionDB) 수정
	 * @param exhibitionDB
	 * @return result (성공한 행의 갯수: 있으면 1, 없으면 0)
	 */
	public int updateExhibitionDB(ExhibitionDB exhibitionDB);





	/** AUTHOR(AuthorDB) 수정
	 * @param authorList
	 * @return result (성공한 행의 갯수: 있으면 1, 없으면 0)
	 */
	public int updateAuthorDB(List<AuthorDB> authorList);





	/** CONTRIBUTOR(ContributorDB) 수정
	 * @param contributor
	 * @return result (성공한 행의 갯수: 있으면 1, 없으면 0)
	 */
	public int updateContributorDB(ContributorDB contributor);
	           	
}
