package edu.og.moa.main.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.og.moa.board.exhibition.model.dto.JsonBoardImage;
import edu.og.moa.board.exhibition.model.service.JsonExhibitionService;
import edu.og.moa.member.model.dto.Member;

@Controller
public class MainController {


    @Autowired
    private JsonExhibitionService jsonExhibitionService;
    
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
    
	

    @GetMapping("/")
    public String mainPage(Model model) {

        // 전시 썸네일 리스트 가져오기
        List<JsonBoardImage> exhibitionList = jsonExhibitionService.selectExhibitionThumbnailList();

        // 모델에 담아서 main.html 로 전달
        model.addAttribute("exhibitionList", exhibitionList);

        return "common/main"; // templates/common/main.html
    }


    
    
    
    
    
    


    
    
    
    
    
    
    
    
    
    
    @GetMapping("/loginError")
    public String loginError(RedirectAttributes ra) {
    	ra.addFlashAttribute("message", "로그인 후 이용해주세요.");
    	// boot에선 필터에선 메세지를 못 보낸다. 그래서 우회했음
    	
    	return "redirect:/";
    }
    
    
    
    
}



