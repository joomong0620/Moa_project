package edu.og.moa.model.service;

import edu.og.moa.model.dto.Member;

public interface MemberService {

	
	/** 회원 가입
	 * @param inputMember
	 * @return result
	 */
	int signUp(Member inputMember);
	
	


}
