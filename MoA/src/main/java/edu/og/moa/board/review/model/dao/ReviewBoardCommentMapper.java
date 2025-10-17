package edu.og.moa.board.review.model.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import edu.og.moa.board.review.model.dto.ReviewComment;

@Mapper
public interface ReviewBoardCommentMapper {

    List<ReviewComment> selectCommentList(int boardNo);

    int insertComment(ReviewComment comment);

    int updateComment(ReviewComment comment);

    int deleteComment(ReviewComment comment);
}
