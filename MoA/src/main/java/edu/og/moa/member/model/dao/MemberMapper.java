package edu.og.moa.member.model.dao;

import org.apache.ibatis.annotations.Mapper;

import edu.og.moa.member.model.dto.Member;

@Mapper
public interface MemberMapper {

	// 회원 가입
	int signUp(Member inputMember);
	
	// 로그인 서비스
	Member login(Member inputMember);

	
	
}
