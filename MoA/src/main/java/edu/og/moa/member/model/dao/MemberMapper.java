package edu.og.moa.member.model.dao;

import org.apache.ibatis.annotations.Mapper;

import edu.og.moa.member.model.dto.Member;

@Mapper
public interface MemberMapper {

	int signUp(Member inputMember);

	
	
}
