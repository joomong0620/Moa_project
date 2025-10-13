package edu.og.moa.member.model.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AjaxMapper {

	int dupCheckId(String memberId);

	int checkEmail(String email);

	int checkNickname(String nickname);

	

	

	

}
