package edu.og.moa.board.performance.model.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PerformanceBoard {
	
	private int boardNo;
	private String boardTitle;
	private String boardContent;
	private int boardCount;
	
	// 공연
	private int pmStartTime;
	private int pmEndTime;
	private int pmPlaytime;
	private int pmHouseNo;
	private int pmTypeNo;
	
	// 공연 시설
	private String pmHouseName;
	private String pmHouseAddress;
	private double pmHouseLat;
	private double pmHouseLong;
	private String pmHouseWebsite;
	private int pmHouseHome;

	// 장르
	private int GenreNo;
	private String GenreName;

	// 좌석 정보
	private String pmPriceType;
	private String pmPrice;
	
	
	private List<PerformanceBoardImage> pmImageList;
	
	private List<PerformanceComment> pmCommentList;
}
