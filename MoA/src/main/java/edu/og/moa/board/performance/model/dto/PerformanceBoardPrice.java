package edu.og.moa.board.performance.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PerformanceBoardPrice {
	
	private String pmPriceType;
	private int pmPrice;
	private int boardNo;
}
