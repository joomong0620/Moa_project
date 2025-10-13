package edu.og.moa.pay.controller;

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
    @GetMapping("/pay")
    public String paymentPage(Model model, HttpSession session) {

        // 임시 로그인 회원으로 하기
        if (session.getAttribute("loginMember") == null) {
            Member fake = new Member();
            fake.setMemberNo(3);
            fake.setMemberNickname("유저일");
            fake.setMemberEmail("test@example.com");
            fake.setMemberTel("01012345678");

            model.addAttribute("member", fake);
        } else {
            Member loginMember = (Member) session.getAttribute("loginMember");
            model.addAttribute("member", loginMember);
        }

        return "pay/pay";
    }

    // 결제 완료 후
    @PostMapping("/complete")
    @ResponseBody  // 여기만 JSON 응답하도록 유지
    public ResponseEntity<?> completePayment(@RequestBody Payment payment) {
        System.out.println("결제 데이터 : " + payment);

        int result = service.insertPayment(payment);

        return ResponseEntity.ok().body(
            java.util.Map.of("result", result)
        );
    }

    // 결제 성공 시 
    @GetMapping("/success")
      public String paymentSuccess() {
        return "pay/pay_success";
    }
}
