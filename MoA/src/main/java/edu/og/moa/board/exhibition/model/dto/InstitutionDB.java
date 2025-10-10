package edu.og.moa.board.exhibition.model.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InstitutionDB {
	private int institutionNo; 
	private String exhibitInstName;
	private String exhibitInstTel;
	private String exhibitLocation;
}
