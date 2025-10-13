package edu.og.moa.member.model.service;

public interface AjaxService {

	
	/** 아이디 중복 검사
	 * @param memberId
	 * @return int
	 */
	int dupCheckId(String memberId);

	/** 이메일 중복 검사
	 * @param email
	 * @return
	 */
	int checkEmail(String email);

	/** 닉네임 중복 검사
	 * @param nickname
	 * @return
	 */
	int checkNickname(String nickname);


}
