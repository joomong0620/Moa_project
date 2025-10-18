package edu.og.moa.board.exhibition.model.exception;

//사용자 정의 예외 
public class ExhibitionInsertException extends RuntimeException{
	
	public ExhibitionInsertException() {
		super("EXHIBITION DB DML 작업 중 예외 발생");
	}
	
	public ExhibitionInsertException(String message) {
		super(message);
	}

}
