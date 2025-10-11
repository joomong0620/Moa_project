package edu.og.moa.member.model.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.og.moa.member.model.dto.Member;

@Repository
public class MemberDAO {
	
	@Autowired
	private MemberMapper memberMapper;

	// 회원 가입
	public int signUp(Member inputMember) {
		
		return memberMapper.signUp(inputMember);
	}
	
	// 로그인 서비스
	public Member login(Member inputMember) {
		
		return memberMapper.login(inputMember);
	}

	

}
