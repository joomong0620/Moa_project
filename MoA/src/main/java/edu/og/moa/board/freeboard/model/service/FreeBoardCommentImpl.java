package edu.og.moa.board.freeboard.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.og.moa.board.freeboard.model.dao.FreeBoardCommentMapper;
import edu.og.moa.board.freeboard.model.dto.Comment;

@Service
public class FreeBoardCommentImpl implements FreeBoardCommentService {
	
	@Autowired
	private FreeBoardCommentMapper mapper;

	// 댓글 목록 조회
	@Override
	public List<Comment> select(int boardNo) {
		return mapper.select(boardNo);
	}
	// 댓글 삽입
	@Override
	public int insert(Comment comment) {
		return mapper.insert(comment);
	}
	
	// 댓글 삭제
	@Override
	public int delete(Comment comment) {
		return mapper.delete(comment);
	}
	// 댓글 수정
	@Override
	public int update(Comment comment) {
		return mapper.update(comment);
	}
	
	
	

}
