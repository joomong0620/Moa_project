package edu.og.moa.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import edu.og.moa.member.model.dto.Member;

@Controller
public class MainController {

    @RequestMapping("/")
    public String mainForward(
        @SessionAttribute(value = "loginMember", required = false) Member loginMember,
        Model model
    ) {
        if (loginMember != null) {
            model.addAttribute("name", loginMember.getMemberNickname()); // 로그인 회원의 닉네임
        } else {
            model.addAttribute("name", null); // 비로그인 상태일 때
        }

        return "common/main";
    }
}
