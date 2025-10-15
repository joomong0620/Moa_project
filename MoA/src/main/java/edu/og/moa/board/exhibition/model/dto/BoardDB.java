package edu.og.moa.board.exhibition.model.dto;


import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardDB {
	private int boardNo;
	private String boardTitle;
	private String boardContent;
	private String bCreateDate;
	private String bUpdateDate;
	private int boardCount; // 게시글 조회수
	private String boardDelFl; 
	private int memberNo;
	private int communityCode;
	private int qCode;
	
	// Exhibition이 현 전시관련 status 체크 2025/10/14
	// (현재진행중 전시, 예정전시, 지난전시) => 이건 myBatis mapper에 resultMap에 포함되지 않는 DTO 필드
	private String eventStatus;    // 분류 결과: pastEvent, futureEvent, currentEvent
}

