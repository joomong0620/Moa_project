package edu.og.moa.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

public class MainController {
	
	@RequestMapping("/")
	public String mainForward(Model model) {
		
		model.addAttribute("name", "홍길동");
		
		return "common/main";
		
		
	}

}
