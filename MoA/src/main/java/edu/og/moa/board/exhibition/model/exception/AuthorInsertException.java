package edu.og.moa.board.exhibition.model.exception;

//사용자 정의 예외 
public class AuthorInsertException extends RuntimeException{
	
	public AuthorInsertException() {
		super("AUTHOR DB DML 작업중 중 예외 발생");
	}
	
	public AuthorInsertException(String message) {
		super(message);
	}

}
