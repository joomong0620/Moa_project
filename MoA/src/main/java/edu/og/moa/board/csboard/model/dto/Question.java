package edu.og.moa.board.csboard.model.dto;

import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.ToString;

@Getter
@Service
@ToString
public class Question {

	private int qCode;
	
	private String qName;
	
}
