package edu.og.moa.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import edu.og.moa.chatting.model.websocket.ChattingWebsocketHandler;


@Configuration // 스프링 설정 클래스
@EnableWebSocket // 웹소켓 기능을 활성화
public class WebSocketConfig implements WebSocketConfigurer{
									// WebSocketCnnfigurer : 웹소켓을 설정하기 위한 인터페이스 
	
	@Autowired
	private ChattingWebsocketHandler chattingWebsocketHandler;
	
	@Autowired
	private HandshakeInterceptor handshakeInterceptor;

	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		
		registry.addHandler(chattingWebsocketHandler, "/chattingSock")
				.addInterceptors(handshakeInterceptor)
				.setAllowedOriginPatterns("http://localhost", "http://127.0.0.1")
				.withSockJS();
		
		
		
	}

}
