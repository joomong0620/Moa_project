package edu.og.moa.board.freeboard.model.exception;

// 사용자 정의 예외 만들기
// -> Exception 관련 클래스를 상속 받으면 된다.

// checked exception   : 예외 처리 필수
// unchecked exception : 예외 처리 선택

// tip. unchecked exception을 만들고 싶은 경우 : RuntimeException 상속 받아서 구현
public class imageDeleteException extends RuntimeException{
	
	public imageDeleteException() {
		super("이미지 삭제 중 예외 발생");
	}
	
	public imageDeleteException(String message) {
		super(message);
	}

}