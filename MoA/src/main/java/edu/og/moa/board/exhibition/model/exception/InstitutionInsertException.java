package edu.og.moa.board.exhibition.model.exception;

//사용자 정의 예외
public class InstitutionInsertException extends RuntimeException{
	
	public InstitutionInsertException() {
		super("INSTITUTION DB DML 작업 중 예외 발생");
	}
	
	public InstitutionInsertException(String message) {
		super(message);
	}

}
