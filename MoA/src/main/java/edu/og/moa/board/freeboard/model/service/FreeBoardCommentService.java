package edu.og.moa.board.freeboard.model.service;

import java.util.List;

import edu.og.moa.board.freeboard.model.dto.Comment;

public interface FreeBoardCommentService {

	
	
	/** 댓글 목록 조회
	 * @param boardNo
	 * @return
	 */
	List<Comment> select(int boardNo);

	/** 댓글 삽입
	 * @param comment
	 * @return
	 */
	int insert(Comment comment);

	
	/** 댓글 삭제
	 * @param comment
	 * @return
	 */
	int delete(Comment comment);

	
	
	/** 댓글 수정
	 * @param comment
	 * @return
	 */
	int update(Comment comment);

}
