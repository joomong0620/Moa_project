package edu.og.moa.common.interceptor;

import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import edu.og.moa.member.model.dto.Member;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ChattingHandshakeInterceptor implements HandshakeInterceptor {

    // WebSocketHandler가 동작하기 전
    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request, 
            ServerHttpResponse response, 
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {
        
        log.info("[HandshakeInterceptor] beforeHandshake 호출");
        
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            
            // 웹소켓 접속한 클라이언트의 세션을 얻어옴
            HttpSession session = servletRequest.getServletRequest().getSession(false);
            
            if (session != null) {
                // 로그인한 회원 정보 가져오기
                Member loginMember = (Member) session.getAttribute("loginMember");
                
                if (loginMember != null) {
                    // WebSocketSession에 memberNo 저장
                    attributes.put("memberNo", loginMember.getMemberNo());
                    // 기존 session도 유지
                    attributes.put("session", session);
                    
                    log.info("[HandshakeInterceptor] memberNo={} 전달 성공", loginMember.getMemberNo());
                    return true;
                } else {
                    log.warn("[HandshakeInterceptor] loginMember가 세션에 없음");
                }
            } else {
                log.warn("[HandshakeInterceptor] HttpSession이 null");
            }
        }
        
        log.warn("[HandshakeInterceptor] 연결 거부 - 로그인 필요");
        return false; // 로그인하지 않은 경우 WebSocket 연결 거부
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request, 
            ServerHttpResponse response, 
            WebSocketHandler wsHandler,
            Exception exception) {
        log.info("[HandshakeInterceptor] afterHandshake 호출");
    }
}