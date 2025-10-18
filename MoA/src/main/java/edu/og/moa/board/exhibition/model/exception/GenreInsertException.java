package edu.og.moa.board.exhibition.model.exception;

//사용자 정의 예외
public class GenreInsertException extends RuntimeException{
	
	public GenreInsertException() {
		super("GENRE DB DML 작업 중 예외 발생");
	}
	
	public GenreInsertException(String message) {
		super(message);
	}

}
