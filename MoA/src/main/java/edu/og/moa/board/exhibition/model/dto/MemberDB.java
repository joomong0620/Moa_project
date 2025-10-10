package edu.og.moa.board.exhibition.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDB {
	private int memberNo;
	private String memberId;
	private String memberEmail;
	private String memberPw;
	private String memberNickname;
	private String memberTel;
	private String memberAddress;
	private String memberAdmin;
	private String profileImg;
	private String memberDelFl;
	
}
