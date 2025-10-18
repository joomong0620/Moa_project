package edu.og.moa.board.exhibition.model.exception;

//사용자 정의 예외 
public class ContributorInsertException extends RuntimeException{
	
	public ContributorInsertException() {
		super("CONTRIBUTOR DB DML작업 중 예외 발생");
	}
	
	public ContributorInsertException(String message) {
		super(message);
	}

}
