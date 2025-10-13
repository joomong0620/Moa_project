package edu.og.moa.member.model.service;

import edu.og.moa.member.model.dto.Member;

public interface MemberService {

	
	/** 회원 가입
	 * @param inputMember
	 * @return result
	 */
	int signUp(Member inputMember);

	
	/** 로그인
	 * @param inputMember
	 * @return id, pw가 일치하는 회원 정보 또는 null
	 */
	Member login(Member inputMember);
	
	


}
