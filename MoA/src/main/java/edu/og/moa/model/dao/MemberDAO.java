package edu.og.moa.model.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.og.moa.model.dto.Member;

@Repository
public class MemberDAO {
	
	@Autowired
	private MemberMapper memberMapper;

	public int signUp(Member inputMember) {
		
		return memberMapper.signUp(inputMember);
	}

	

}
