package edu.og.moa.member.model.service;

import edu.og.moa.member.model.dto.Member;

public interface MemberService {

	
	/** 회원 가입
	 * @param inputMember
	 * @return result
	 */
	int signUp(Member inputMember);
	
	


}
