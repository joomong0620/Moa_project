package edu.og.moa.main.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import edu.og.moa.board.exhibition.model.dto.JsonBoardImage;
import edu.og.moa.board.exhibition.model.service.ExhibitionService;
import edu.og.moa.board.exhibition.model.service.JsonExhibitionService;
import edu.og.moa.board.performance.model.dto.PerformanceBoardImage;
import edu.og.moa.board.performance.model.service.PerformanceService;
import edu.og.moa.member.model.dto.Member;

@Controller
public class MainController {


    @Autowired
    private JsonExhibitionService jsonExhibitionService;

    @Autowired
    private PerformanceService performanceService;


    @GetMapping("/")
    public String mainPage(
        @SessionAttribute(value = "loginMember", required = false) Member loginMember,
        Model model
    ) {
        if (loginMember != null)
            model.addAttribute("name", loginMember.getMemberNickname());
        else
            model.addAttribute("name", null);

        // 전시 썸네일
        List<JsonBoardImage> exhibitionList = jsonExhibitionService.selectExhibitionThumbnailList();
        model.addAttribute("exhibitionList", exhibitionList);

        // 공연 썸네일
        List<PerformanceBoardImage> performanceList = performanceService.selectPerformanceList();
        model.addAttribute("performanceList", performanceList);

        return "common/main";
    }
    
}