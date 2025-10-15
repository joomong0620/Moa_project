package edu.og.moa.pay.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import edu.og.moa.member.model.dto.Member;
import edu.og.moa.pay.model.dto.Payment;
import edu.og.moa.pay.model.service.PaymentService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/payment")
public class PaymentController {

	@Autowired
	private PaymentService service;

	// 결제창
	@PostMapping("/pay")
	public String paymentPage(Model model, HttpSession session, @ModelAttribute Payment ticketingInfo) {

		Member loginMember = (Member) session.getAttribute("loginMember");

		// 로그인하지 않은 경우 로그인 페이지로
		if (loginMember == null) {
			return "redirect:/member/login";
		}

		// 로그인된 회원 정보 모델에 담기
		model.addAttribute("member", loginMember);
		model.addAttribute("ticketingInfo", ticketingInfo);

		System.out.println("넘어온 데이터 : " + ticketingInfo);

		return "pay/pay";
	}

	// 결제 완료 후
	@PostMapping("/complete")
	@ResponseBody
	public ResponseEntity<?> completePayment(@RequestBody Payment payment, HttpSession session) {

		Member loginMember = (Member) session.getAttribute("loginMember");
		if (loginMember == null) {
			return ResponseEntity.status(401).body("로그인이 필요합니다.");
		}

		// 결제한 회원 번호 세팅
		payment.setMemberNo(loginMember.getMemberNo());

		System.out.println("결제 데이터 : " + payment);

		int result = service.insertPayment(payment);

		return ResponseEntity.ok().body(java.util.Map.of("result", result));
	}

	// 결제 성공 시
	@GetMapping("/success")
	public String paymentSuccess() {
		return "pay/pay_success";
	}

	// 결제 취소 요청
	@PostMapping("/cancel")
	@ResponseBody
	public ResponseEntity<?> cancelPayment(@RequestBody Map<String, String> requestData, HttpSession session) {
		Member loginMember = (Member) session.getAttribute("loginMember");

		if (loginMember == null) {
			return ResponseEntity.status(401).body("로그인이 필요합니다.");
		}

		String impUid = requestData.get("impUid");
		String reason = requestData.getOrDefault("reason", "사용자 요청");

		try {
			boolean cancelResult = service.cancelPayment(impUid, reason);
			if (cancelResult) {
				return ResponseEntity.ok().body(Map.of("result", "success"));
			} else {
				return ResponseEntity.status(500).body(Map.of("result", "fail"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
		}
	}
	
	// 취소 성공 시
	@GetMapping("/cancel/success")
	public String paymentCancel() {
		return "pay/pay_cancel";
	}

}
