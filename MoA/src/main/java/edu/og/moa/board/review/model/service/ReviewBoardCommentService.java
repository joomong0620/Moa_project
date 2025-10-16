package edu.og.moa.board.review.model.service;

import java.util.List;
import edu.og.moa.board.review.model.dto.ReviewComment;

public interface ReviewBoardCommentService {

    List<ReviewComment> selectCommentList(int boardNo);

    int insertComment(ReviewComment comment);

    int updateComment(ReviewComment comment);

    int deleteComment(ReviewComment comment);
}
