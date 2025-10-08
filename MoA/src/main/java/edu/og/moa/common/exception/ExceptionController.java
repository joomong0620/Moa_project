package edu.og.moa.common.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice 
public class ExceptionController {
	
	@ExceptionHandler(Exception.class)  
	public String exceptionHandler(Exception e) {
		e.printStackTrace(); 
		return "error/500"; 
	}

}

