package edu.og.moa.board.review.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReviewImage {

    private int imgNo;         // 이미지 번호 (PK)
    private int reviewNo;      // 리뷰 번호 (FK)
    private String imgPath;    // 이미지 저장 경로
    private String imgOrig;    // 원본 파일명
    private String imgRename;  // 변경된 파일명
    private int imgOrder;      // 이미지 순서
}
