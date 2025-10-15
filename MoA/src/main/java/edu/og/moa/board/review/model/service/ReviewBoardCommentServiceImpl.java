package edu.og.moa.board.review.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.og.moa.board.review.model.dao.ReviewBoardCommentMapper;
import edu.og.moa.board.review.model.dto.ReviewComment;

@Service
public class ReviewBoardCommentServiceImpl implements ReviewBoardCommentService {

    @Autowired
    private ReviewBoardCommentMapper mapper;

    @Override
    public List<ReviewComment> selectCommentList(int boardNo) {
        return mapper.selectCommentList(boardNo);
    }

    @Override
    public int insertComment(ReviewComment comment) {
        return mapper.insertComment(comment);
    }

    @Override
    public int updateComment(ReviewComment comment) {
        return mapper.updateComment(comment);
    }

    @Override
    public int deleteComment(ReviewComment comment) {
        return mapper.deleteComment(comment);
    }
}
