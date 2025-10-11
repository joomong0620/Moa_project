package edu.og.moa.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.og.moa.member.model.dao.AjaxDAO;

@Service
public class AjaxServiceIm implements AjaxService{

	@Autowired
	private AjaxDAO dao;

	// 아이디 중복 검사
	@Override
	public int dupCheckId(String memberId) {
		
		return dao.dupCheckId(memberId);
	}
	
	
}
