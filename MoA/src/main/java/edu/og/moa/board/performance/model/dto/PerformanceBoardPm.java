package edu.og.moa.board.performance.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PerformanceBoardPm {

	private int boardNo;
	private int genreNo;
	private String pmStartTime;
	private String pmEndTime;
	private String pmPlaytime;
	private int pmHouseNo;
	
	
}
