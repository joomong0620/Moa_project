package edu.og.moa.myPage.model.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.og.moa.member.model.dto.Member;


public interface MyPageService  {

	/** 내 정보 수정
	 * @param updateMember
	 * @return
	 */
	int updateInfo(Member updateMember);

	
	
	/** 프로필 이미지 수정
	 * @param profileImage
	 * @param loginMember
	 * @return
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	int updateProfile(MultipartFile profileImg, Member loginMember) 
			throws IllegalStateException, IOException;



	/** 회원 탈퇴
	 * @param memberNo
	 * @param memberPw
	 * @return
	 */
	int secession(int memberNo, String memberPw);




	
	
}
