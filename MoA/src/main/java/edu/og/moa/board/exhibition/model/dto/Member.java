package edu.og.moa.board.exhibition.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Member {

	private int memberNo;
	private String memberEmail;
	private String memberPw;
	private String memberNickname;
	private String memberTel;
	private String memberAddress;
	private String profileImage;
	private String enrollDate;
	private String memberDeleteFlag;
	private int authority;
	   
}
