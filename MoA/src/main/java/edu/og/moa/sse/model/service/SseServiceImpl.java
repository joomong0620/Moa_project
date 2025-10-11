package edu.og.moa.sse.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.og.moa.sse.dto.Notification;
import edu.og.moa.sse.model.mapper.SseMapper;

@Service
public class SseServiceImpl implements SseService{

	@Autowired
	private SseMapper mapper;
	//알림 삽입 후 알림 받을 회원 번호 + 알림 개수 반환
	@Override
	public Map<String, Object> insertNotification(Notification notification) {
		
        // 매개변수 notification에 저장된 값:
        // -> type, url, pkNo(게시글 번호), content, sendMemberNo

        // pkNo를 이용해서 실제 "게시글 작성자" 번호를 조회
        int receiveMemberNo = mapper.selectBoardWriterNo(notification.getPkNo());

        // 자기 자신이라면 알림 생성하지 않음
        if (notification.getSendMemberNo() == receiveMemberNo) {
            System.out.println("자기 자신에게는 알림이 전송되지 않습니다.");
            return null;
        }

        // 알림 DTO에 수신자 번호 세팅
        notification.setReceiveMemberNo(receiveMemberNo);

        // DB에 알림 저장
        int result = mapper.insertNotification(notification);

        Map<String, Object> map = null;

        if (result > 0) {
            // 수신자 번호 + 읽지 않은 알림 개수 조회
            map = mapper.selectReceiveMember(notification.getNotificationNo());
        }

        return map;
    }

	
	//로그인한 회원의 알림 목록 조회
	@Override
	public List<Notification> selectNotificationList(int memberNo) {
		return mapper.selectNotificationList(memberNo);
	}

	//읽지 않은 알람 개수 조회
	@Override
	public int notReadCheck(int memberNo) {
		return mapper.notReadCheck(memberNo);
	}

	//알람 삭제
	@Override
	public void deleteNotification(int notificationNo) {
		mapper.deleteNotification(notificationNo);
	}

	@Override
	public void updateNotification(int notificationNo) {
		mapper.updateNotification(notificationNo);
		
	}

}
