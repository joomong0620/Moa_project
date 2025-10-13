package edu.og.moa.chatting.model.service;

import java.util.List;
import java.util.Map;

import edu.og.moa.chatting.model.dto.chattingRoomkjy;
import edu.og.moa.chatting.model.dto.Messagekjy;
import edu.og.moa.member.model.dto.Member;

public interface ChattingService {

	
	
	/** 메세지 삽입
	 * @param msg
	 * @return result
	 */
	public int insertMessage(Messagekjy msg);

	/** 채팅 목록 조회
	 * @param memberNo
	 * @return memberList
	 */
	public List<chattingRoomkjy> selectRoomList(int memberNo);

	/** 채팅 상대 검색
	 * @param map
	 * @return
	 */
	public List<Member> selectTarget(Map<String, Object> map);

	
	/** 개인 채팅 입장 (생성)
	 * @param of
	 * @return
	 */
	public int checkChattingNo(Map<String, Integer> of);

	/** 팀 채팅 입장 (생성)
	 * @param memberList
	 * @return
	 */
	public int createTeamRoom(List<Integer> memberList);

	
	
	/** 채팅방 읽음 표시
	 * @param paramMap
	 * @return
	 */
	public int updateReadFlag(Map<String, Object> paramMap);

	
	
	/** 채팅방 메세지 목록 조회
	 * @param paramMap
	 * @return
	 */
	public List<Messagekjy> selectMessageList(Map<String, Object> paramMap);

	
	
	/** 채팅방 나가기 
	 * @param paramMap
	 * @return
	 */
	public int exitRoom(Map<String, Object> paramMap);

	public List<Integer> getChattingRoomMembers(int chattingNo);


}
