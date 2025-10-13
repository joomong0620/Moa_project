package edu.og.moa.member.model.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AjaxDAO {
	
	@Autowired
	private AjaxMapper mapper;
	
	
	public int dupCheckId(String memberId) {
		
		return mapper.dupCheckId(memberId);
	}


	public int checkEmail(String email) {
		return mapper.checkEmail(email);
	}


	public int checkNickname(String nickname) {
		return mapper.checkNickname(nickname);
	}

}
