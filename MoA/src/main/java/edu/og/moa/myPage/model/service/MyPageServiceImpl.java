package edu.og.moa.mypage.model.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.og.moa.board.freeboard.model.dto.Board;
import edu.og.moa.common.utility.Util;
import edu.og.moa.member.model.dto.Member;
import edu.og.moa.mypage.model.dao.MyPageMapper;
import edu.og.moa.pay.model.dto.Payment;

@Service
@PropertySource("classpath:/config.properties")
public class MyPageServiceImpl implements MyPageService {

	
	@Value("${my.member.webpath}")
	private String webPath;
	
	@Value("${my.member.location}")
	private String filePath;
	
	
	@Autowired
	private MyPageMapper mapper;


	// 내 정보 수정 서비스
	@Transactional(rollbackFor=Exception.class)
	@Override
	public int updateInfo(Member updateMember) {
		return mapper.updateInfo(updateMember);
	}


	// 프로필 이미지 수정
	@Override
	public int updateProfile(MultipartFile profileImg, Member loginMember) throws IllegalStateException, IOException {
		
		// 프로필 이미지 변경 실패 대비
		String prevImg = loginMember.getProfileImg(); // 이전 이미지 저장
		String rename = null; // 이미지 변경명 저장 변수
		
		// 업로드된 이미지가 있을 경우
		if(profileImg.getSize()>0) {
			
			//1) 파일명 면경
			rename = Util.fileRename(profileImg.getOriginalFilename());
			
			
			// 바뀐 이름을 loginMember에 세팅
			loginMember.setProfileImg(webPath + rename);
		} else { // 없을 경우(X 버튼 클릭)
			loginMember.setProfileImg(null);
		}
		
		int result = mapper.updateProfile(loginMember); 
		
		if(result > 0) { // 이미지 수정 성공 시
			
			// 새 이미지가 업로드 된 경우
			if(rename !=null) profileImg.transferTo(new File(filePath + rename));
			
			
		}else {
			loginMember.setProfileImg(prevImg);
		}
		
		return result;
	}


	@Override
	public int secession(int memberNo, String memberPw) {
		
		// 1. 로그인한 회원의 비밀번호 조회
		String dbPw = mapper.selectEncPw(memberNo);
		
		// 2. 비밀번호 일치 시 회원 탈퇴 진행
		if(memberPw.equals(dbPw)) {
			return mapper.secession(memberNo);
		}
		
		// 3. 비밀번호 불일치 시 0 반환
		return 0;
	}


	@Override
	public List<Board> selectLikeBoard(int memberNo) {
		return mapper.selectLikeBoard(memberNo);
	}

	
	@Override
	public List<Board> selectMyBoardList(int memberNo) {
		return mapper.selectMyBoardList(memberNo);
	}

	// 내 예매내역 불러오기
	@Override
	public List<Payment> selectPaymentList(int memberNo) {
		return mapper.selectPaymentList(memberNo);
	}

	// 예매 결제 취소
	@Override
	public int cancelPayment(String payNo) {
		 return mapper.cancelPayment(payNo);
	}


	

}
