package edu.og.moa.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.og.moa.model.dao.MemberDAO;
import edu.og.moa.model.dto.Member;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MemberServiceImpl implements MemberService{
	
	
	@Autowired
	private MemberDAO dao;

	@Override
	public int signUp(Member inputMember) {
		
		int result = dao.signUp(inputMember);
		
		return result;
	}
	
	
	
	
	

}
