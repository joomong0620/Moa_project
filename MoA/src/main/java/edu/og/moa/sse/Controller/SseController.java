package edu.og.moa.sse.Controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import edu.og.moa.member.model.dto.Member;
import edu.og.moa.sse.dto.Notification;
import edu.og.moa.sse.model.service.SseService;


@RestController // @Controller + @ResponseBody
            // #요청 온 곳으로 돌아간다.
public class SseController {

   @Autowired
   private SseService service;
   
   private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
   // SseEmitter : 서버로부터 메세지를 전달 받을 클라이언트 정보를 저장한 객체 == 연결된 클라이언트
   
   // ConcurrentHashMap : 멀티스레드 환경에서 동기화를 보장하는 Map
   // -> 한 번에 많은 요청이 있어도 차례대로 처리 - 안전성 보장
   
   // 클라이언트 연결 요청 처리
   @GetMapping("/sse/connect")
   public SseEmitter sseConnect(@SessionAttribute("loginMember") Member loginMember) {
      
      // Map에 저장될 Key 값으로  회원 번호 얻어오기
      String clientId = loginMember.getMemberNo() + "";
      
      // SseEmitter 객체 생성
      // -> 연결 대기 시간 10분 설정 (ms 단위)
      SseEmitter emitter = new SseEmitter(10 * 60 * 1000L); // 10분, ms단위
      
      // 클라이언트 정보를 Map에 추가
      emitters.put(clientId, emitter);
      
      // 클라이언트 연결 종료 시 Map 에서 제거
      emitter.onCompletion(()->emitters.remove(clientId));
      // # 다른 매개변수로 넘겨보냄
      
      //클라이언트 타임 아웃 시 Map에서 제거
      emitter.onTimeout(()-> emitters.remove(clientId));
      
      return emitter;
      
      
   }
   //알림 메세지 전송
   @PostMapping("/sse/send")
   public void sendNotification(
         @SessionAttribute("loginMember") Member loginMember,
         @RequestBody Notification notification) {
                  //#DTO : 키값이 일치하는 필드에 들어갈 것이다.
      
      // 알림 보낸 회원(현재 로그인한 회원)번호 추가
      notification.setSendMemberNo(loginMember.getMemberNo());
      
      // 전달 받은 알림 데이터를 DB에 저장하고
      // 알림 받을 회원의 번호 + 해당 회원의 읽지 않은 알림 개수 반환 받는 서비스 호출

      Map<String, Object> map = service.insertNotification(notification);
                                    //notification을 매개변수로 전달
      // 알림을 받을 클라이언트 id
      String clientId =map.get("receiveMemberNo").toString(); //문자열로 바뀜
      
      // 연결된 클라이언트 대기 명단(emitters)에서
      // clientId가 일치하는 클라이언트 찾기
      SseEmitter emitter = emitters.get(clientId);
      
      // clientId가 일치하는 클라이언트가 있을 경우
      if(emitter != null) {
         try {
            emitter.send(map); //#알림 받을 회원 번호랑, 알림 몇개 있는지 들어있다.
            
         }catch(Exception e) {
            emitters.remove(clientId);
             
         }
      }
      
      
   }
   
   
   //로그인한 회원의 알림 목록 조회
   @GetMapping("/notification")
   public List<Notification> selectNotificationList(
       @SessionAttribute("loginMember") Member loginMember
   ){
       int memberNo = loginMember.getMemberNo();
       return service.selectNotificationList(memberNo);
   }
   
   
   //현재 로그인한 회원이 받은 알림 중 읽지 않은 알림 개수 조회
   @GetMapping("/notification/notReadCheck")
   public int notReadCheck(
         @SessionAttribute("loginMember") Member loginMember
   ){
       return service.notReadCheck(loginMember.getMemberNo());
   }
   
   
   
   // 알림 삭제
   @DeleteMapping("/notification")
   public void deleteNotification(@RequestBody int notificationNo) {
       service.deleteNotification(notificationNo);
   }

   
   // 알림 읽음 여부 변경
   @PutMapping("/notification")
   public void updateNotification(@RequestBody int notificationNo) {
      service.updateNotification(notificationNo);
   }
   
}
   

   