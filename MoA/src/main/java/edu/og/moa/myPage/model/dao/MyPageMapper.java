package edu.og.moa.myPage.model.dao;

import org.apache.ibatis.annotations.Mapper;

import edu.og.moa.member.model.dto.Member;

@Mapper
public interface MyPageMapper {

	
	// 내 정보 수정
	int updateInfo(Member updateMember);

	
	// 프로필 이미지 수정
	int updateProfile(Member loginMember);


	// 비밀번호 조회
	String selectEncPw(int memberNo);


	// 회원 탈퇴	
	int secession(int memberNo);




	
}
