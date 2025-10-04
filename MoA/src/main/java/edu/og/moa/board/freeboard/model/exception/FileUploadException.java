package edu.og.moa.board.freeboard.model.exception;

// 사용자 정의 예외 만들기
// -> Exception 관련 클래스를 상속 받으면 된다.

// checked exception   : 예외 처리 필수
// unchecked exception : 예외 처리 선택

// tip. unchecked exception을 만들고 싶은 경우 : RuntimeException 상속 받아서 구현
public class FileUploadException extends RuntimeException{
	
	public FileUploadException() {
		super("파일 업로드 중 예외 발생");
	}
	
	public FileUploadException(String message) {
		super(message);
	}

}
