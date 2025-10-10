package edu.og.moa.board.freeboard.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import edu.og.moa.board.freeboard.model.dto.Comment;

@Mapper
public interface FreeBoardCommentMapper {

	
	// 댓글 목록 조회
	public List<Comment> select (int boardNo);

	// 댓글 삽입
	public int insert(Comment comment);

	// 댓글 삭제
	public int delete(Comment comment);

	
	// 댓글 수정
	public int update(Comment comment);
	
	
}
