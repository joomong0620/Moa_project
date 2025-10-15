package edu.og.moa.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.og.moa.member.model.dao.MemberDAO;
import edu.og.moa.member.model.dto.Member;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MemberServiceImpl implements MemberService{
	
	
	@Autowired
	private MemberDAO dao;

	// 회원 가입
	@Override
	public int signUp(Member inputMember) {
		
		System.out.println("inputMember2 : " + inputMember);
		
		int result = dao.signUp(inputMember);
		
		System.out.println("inputMember3 : " + inputMember);
		
		return result;
	}

	// 로그인 서비스
	@Override
	public Member login(Member inputMember) {
		
		Member loginMember = dao.login(inputMember);
		
		if(loginMember != null) {// 로그인 성공 시
			
			if(inputMember.getMemberPw().equals(loginMember.getMemberPw())) {
				
				loginMember.setMemberPw(null);
			}else {
				loginMember = null;
			}
		}
		
		return loginMember;
	}
	
	
	
	
	

}
