package edu.og.moa.myPage.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import edu.og.moa.board.freeboard.model.dto.Board;
import edu.og.moa.member.model.dto.Member;
import edu.og.moa.pay.model.dto.Payment;

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

	// 좋아요한 게시물
	List<Board> selectLikeBoard(int memberNo);


	// 내가쓴 게시물
	List<Board> selectMyBoardList(int memberNo);

	// 결제(예매) 내역 조회
	List<Payment> selectPaymentList(int memberNo);

	// 예매 취소
	int cancelPayment(String payNo);




	
}
