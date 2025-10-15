package edu.og.moa.mypage.model.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.og.moa.board.freeboard.model.dto.Board;
import edu.og.moa.member.model.dto.Member;
import edu.og.moa.pay.model.dto.Payment;


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



	/** 좋아요한 게시물
	 * @param memberNo
	 * @return
	 */
	List<Board> selectLikeBoard(int memberNo);



	/** 내가 쓴 게시물
	 * @param memberNo
	 * @return
	 */
	List<Board> selectMyBoardList(int memberNo);


	
	/** 결제(예매) 내역 조회
	 * @param memberNo
	 * @return
	 */
	List<Payment> selectPaymentList(int memberNo);


	/** 예매 취소
	 * @param payNo
	 * @return
	 */
	int cancelPayment(String payNo);




	
	
}
