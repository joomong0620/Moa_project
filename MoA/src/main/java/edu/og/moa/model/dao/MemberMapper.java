package edu.og.moa.model.dao;

import org.apache.ibatis.annotations.Mapper;

import edu.og.moa.model.dto.Member;

@Mapper
public interface MemberMapper {

	int signUp(Member inputMember);

	
	
}
