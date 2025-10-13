package edu.og.moa.chatting.model.websocket;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import edu.og.moa.chatting.model.dto.Messagekjy;
import edu.og.moa.chatting.model.service.ChattingService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ChattingWebsocketHandler extends TextWebSocketHandler {
    
    @Autowired
    private ChattingService service;
    
    // 모든 연결된 세션 관리 (memberNo → WebSocketSession)
    private final Map<Integer, WebSocketSession> allSessions = new ConcurrentHashMap<>();
    
    // Jackson ObjectMapper (JSON 파싱용)
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // ======= 연결 시작 =======
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("[연결됨] {}", session.getId());
        
        // HttpSession에서 memberNo 가져오기
        Map<String, Object> attributes = session.getAttributes();
        Integer memberNo = (Integer) attributes.get("memberNo");
        
        if (memberNo != null) {
            allSessions.put(memberNo, session);
            log.info("[세션 등록] memberNo={}, 총 세션 수={}", memberNo, allSessions.size());
        } else {
            log.warn("[세션 등록 실패] memberNo 없음");
        }
    }
    
    // ======= 메시지 수신 처리 =======
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        log.info("수신된 JSON: {}", textMessage.getPayload());
        
        // JSON → DTO 매핑
        Messagekjy msg = objectMapper.readValue(textMessage.getPayload(), Messagekjy.class);
        msg.setMemberNo(msg.getSenderNo()); // memberNo 세팅 (DB 저장용)
        
        log.info("[수신 메시지] chattingNo={}, senderNo={}, content={}", 
                 msg.getChattingNo(), msg.getSenderNo(), msg.getMessageContent());
        
        // 1. DB 저장
        int result = service.insertMessage(msg);
        
        if (result > 0) {
            // 전송 시간 포맷
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
            msg.setSendTime(sdf.format(new Date()));
            
            // 2. 해당 채팅방에 속한 모든 멤버에게 브로드캐스트
            int chattingNo = msg.getChattingNo();
            
            // 채팅방 참여자 목록 가져오기
            List<Integer> memberList = service.getChattingRoomMembers(chattingNo);
            
            int successCount = 0;
            for (Integer memberNo : memberList) {
                WebSocketSession memberSession = allSessions.get(memberNo);
                if (memberSession != null && memberSession.isOpen()) {
                    try {
                        memberSession.sendMessage(new TextMessage(new Gson().toJson(msg)));
                        successCount++;
                    } catch (Exception e) {
                        log.error("[전송 실패] memberNo={}", memberNo, e);
                    }
                }
            }
            
            log.info("[브로드캐스트 완료] 채팅방 {} - 전송 성공 {}/{}명", 
                     chattingNo, successCount, memberList.size());
        }
    }
    
    // ======= 연결 종료 시 =======
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Map<String, Object> attributes = session.getAttributes();
        Integer memberNo = (Integer) attributes.get("memberNo");
        
        if (memberNo != null) {
            allSessions.remove(memberNo);
            log.info("[연결 종료] memberNo={}, 남은 세션 수={}", memberNo, allSessions.size());
        } else {
            log.info("[연결 종료] {}", session.getId());
        }
    }
}