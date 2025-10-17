package edu.og.moa.board.exhibition.model.dto;

import java.util.List;

import edu.og.moa.board.performance.model.dto.PerformanceBoardPrice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TicketingInfo { // 예매/결제로 넘겨주는 정보

	private String showTitle; 	// ${showTitle}	결제창 상단 표시용 제목
	private String showName;  	// ${showName} 공연/전시 이름		
	private String showDate;	// ${showDate}	공연/전시 일시
	private String showPlace;	// ${showPlace}	공연장 또는 전시장 이름
	private String memberNickname; // ${member.memberNickname} 구매자 이름
	private String memberTel; 	 // ${member.memberTel}	 연락처
	private String memberEmail;  //  ${member.memberEmail} 이메일

	private String eventType;   // performance(pm) or exhibition (exhb)
	private String PriceType; // ${p.pmPriceType} 좌석/분류명 (VIP, R, S 등..)
	private String Price; 	// ${p.pmPrice} 실제 금액 (정수로); // 전시의 경우는 좌석 분류 없이 0원 아니면 금액으로 넘어간다
	private List<PerformanceBoardPrice> priceList;
	
}
