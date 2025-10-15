package edu.og.moa.board.exhibition.model.dto;

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
// Exhibition DTO의 중간 매개 DTO
public class EventPeriod { 
	private int exhibitNo; // => exhibition.getExhibitNo() ( == board.getBoardNo())
	private String eventPeriod; // 기간 문자열 from Exhibition.getExhibitDate()
    private String eventStatus; // 분류 결과: pastEvent, futureEvent, currentEvent ==> Exhibition DTO에 필드로 assign했음(이건 mapper의 resultMap에 대응하지 않는 걸로)
}
