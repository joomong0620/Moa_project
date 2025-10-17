package edu.og.moa.pay.model.dto;

import java.util.Date;
import java.util.List;

import edu.og.moa.board.performance.model.dto.PerformanceBoardPrice;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Payment {

    private String impUid;      // PortOne 결제 고유번호
    private String merchantUid; // 주문번호
    private int memberNo;       // 결제자 회원 번호
    private int boardNo;        // 공연/전시 번호
    private int payMuch;        // 결제 금액
    private String payWhat;     // 결제 수단
    private String payOk;       // 결제 성공 여부
    private String payDate;     // 결제 날짜 (SYSDATE)
    
    
    
    // fields to be filled by performance or exhibition side
    private String showTitle;    // ${showTitle}   결제창 상단 표시용 제목
    private String showName;     // ${showName} 공연/전시 이름      
    private String showDate;   // ${showDate}   공연/전시 일시
    private String showPlace;   // ${showPlace}   공연장 또는 전시장 이름
    private String memberNickname; // ${member.memberNickname} 구매자 이름
    private String memberTel;     // ${member.memberTel}    연락처
    private String memberEmail;  //  ${member.memberEmail} 이메일

    private String eventType;   // performance(pm) or exhibition (exhb)
    private String PriceType; // ${p.pmPriceType} 좌석/분류명 (VIP, R, S 등..)
    private List<PerformanceBoardPrice> priceList;
    
    
    
    
    // JOIN으로 가져올 추가 필드들
    private String title;        // 공연/전시 제목 (BOARD.BOARD_TITLE)
    private String thumbnail;    // 썸네일 (IMG 테이블에서)
    private String place;        // 장소 (공연정보 테이블에서)
    private String period;         // 공연 기간 (공연정보 테이블에서)
    private Date seeDate;
    private String boardCode; 
    
    private String pmPriceType;
    private int pmPrice;
    
    // HTML에서 rsvNo로 사용
    public String getRsvNo() {
        return this.impUid;
        
        
    }
}