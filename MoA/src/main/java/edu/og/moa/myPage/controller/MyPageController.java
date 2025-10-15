package edu.og.moa.myPage.controller;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.og.moa.board.freeboard.model.dto.Board;
import edu.og.moa.member.model.dto.Member;
import edu.og.moa.myPage.model.service.MyPageService;
import edu.og.moa.pay.model.dto.Payment;
import edu.og.moa.pay.model.service.PaymentService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@SessionAttributes("loginMember")
@RequestMapping("/mypage")
@Controller 
public class MyPageController {
	
	@Autowired
	private MyPageService service;
	
	
	// 내정보 페이지 이동 + 좋아요한 게시물, 내가 쓴 게시물, 예약한 내역 조회
	@GetMapping("/info")
	public String myPageInfo(
			@SessionAttribute("loginMember") Member loginMember
			, Model model
			) {
		 int memberNo = loginMember.getMemberNo();
		 
		 // 좋아요한 게시물
		 List<Board> likedList = service.selectLikeBoard(memberNo);
		 
		 // 내가 쓴 게시물
		 List<Board> myPostList = service.selectMyBoardList(memberNo);
		 
		 // 결제(예매) 내역 조회
	     List<Payment> reservationList = service.selectPaymentList(memberNo);
		 
	     
	     model.addAttribute("likedList", likedList);
		 model.addAttribute("myPostList", myPostList);
		 model.addAttribute("reservationList", reservationList);
		 
		 return "myPage/myPage";
		 
		 
	
	
	}
	
	// 내 정보 수정
	@PostMapping("/info")
	public String info(Member updateMember, String[] memberAddr,
	                   @SessionAttribute("loginMember") Member loginMember,
	                   RedirectAttributes ra) {

	    if(memberAddr != null && memberAddr.length > 0) {
	        String addr = String.join("^^^", memberAddr);
	        if(addr.replaceAll("\\^\\^\\^", "").trim().isEmpty()) {
	            updateMember.setMemberAddr(null);
	        } else {
	            updateMember.setMemberAddr(addr);
	        }
	    } else {
	        updateMember.setMemberAddr(null);
	    }

	    updateMember.setMemberNo(loginMember.getMemberNo());

	    int result = service.updateInfo(updateMember);
	    String message = result != 0 ? "회원 정보가 수정되었습니다." : "회원 정보 수정 실패 ㅠㅠ";

	    if(result != 0) {
	        loginMember.setMemberNickname(updateMember.getMemberNickname());
	        loginMember.setMemberAddr(updateMember.getMemberAddr());
	    }

	    ra.addFlashAttribute("message", message);

	    return "redirect:info";
	}
	
	
	//프로필 이미지 수정
	@PostMapping("/profile")
	public String updateProfile (
		@RequestParam("profileImg") MultipartFile profileImg // 업로드 파일
		, @SessionAttribute("loginMember") Member loginMember
		, RedirectAttributes ra //리다이렉트 시 메세지 전달
		) throws IllegalStateException, IOException {
		
		// 프로필 이미지 수정 서비스 호출
		int result
			= service.updateProfile(profileImg, loginMember);
		
		String message = null;
		
		
		if(result != 0) message = "프로필 이미지가 변경되었습니다.";
		else 			message = "프로필 이미지 변경 실패 ㅠㅠ";
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:info";
	}
	
	// 회원 탈퇴
		@PostMapping("/secession")
		public String secession(@SessionAttribute("loginMember") Member loginMember
				, String memberPw, SessionStatus status, RedirectAttributes ra
				, HttpServletResponse resp) {
			
			// 1. 로그인한 회원의 회원 번호 얻어오기
			int memberNo = loginMember.getMemberNo();
			
			// 2. 회원 탈퇴 서비스 호출
			int result = service.secession(memberNo, memberPw);
			
			String message = null;
			String path = "redirect:";
			
			if(result != 0) {
				// 3. 탈퇴 성공 시
				
				message = "탈퇴 되었습니다."; // - message : 탈퇴 되었습니다.
				status.setComplete(); // - 로그아웃
				path += "/"; // - 메인페이지로 리다이렉트
				
				// + 쿠키 삭제
				Cookie cookie = new Cookie("saveId", ""); // 만약 같은 이름의 쿠키가 존재하면 덮어씌움
				cookie.setMaxAge(0); // 기존 쿠키 삭제
				cookie.setPath("/"); // 클라이언트가 어떤 요청을 할 때, 쿠키가 첨부될 지 경로(주소) 지정
				resp.addCookie(cookie); // 응답 객체를 이용해서 쿠키를 클라이언트에게 전달
										// -> 클라이언트 컴퓨터에 파일로 생성
			} else {
				// 4. 탈퇴 실패 시 
				message = "현재 비밀번호가 일치하지 않습니다.";
				// - 회원 탈퇴 페이지로 리다이렉트
				path += "info";
			}
			
			ra.addFlashAttribute("message", message);
			return path;
			
			
		}
		// ========== 예매 관련 추가 ==========
		
		// 예매(결제) 취소
		@PostMapping("/cancel/{payNo}")
		@ResponseBody
		public String cancelPayment(@PathVariable("payNo") String payNo) {
			int result = service.cancelPayment(payNo);
			return (result > 0) ? "예매가 취소되었습니다." : "취소 실패";
		}

	
}