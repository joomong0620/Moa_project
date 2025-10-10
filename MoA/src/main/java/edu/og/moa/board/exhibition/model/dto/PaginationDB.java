package edu.og.moa.board.exhibition.model.dto;

public class PaginationDB { //
	// 페이지네이션(페이징 처리)에 필요한 모든 값을 저장하고 있는 객체

	// fields
	private int currentPage;      // 현재 페이지 <=== 동적 계산에 이것 필요(필수값)
	private int listCount;         // 전체 게시글 수 <=== 동적 계산에 이것 필요(필수값)

	private int limit = 10;         // 한 페이지에 보여질 게시글 수 (20개씩 또는 50개씩 보기할때 바꿀값) (우리 프로젝트에서는 이값 "고정")
	private int pageSize = 10;       // 목록 하단 페이지 번호의 노출 개수 (우리 프로젝트에서는 이값 "고정")

	
	// 위 4개 값으로 아래 5개 값을 계산한다.
	private int maxPage;         // 제일 큰 페이지 번호 == 마지막 페이지 번호
	private int startPage;         // 목록 하단에 노출된 페이지의 시작 번호
	private int endPage;         // 목록 하단에 노출된 페이지의 끝 번호

	private int prevPage;         // 목록 하단에 노출된 번호의 이전 목록 끝 번호
	private int nextPage;         // 목록 하단에 노출된 번호의 다음 목록 시작 번호
	
	// 기본생성자 말고, 매개변수 생성자만 만든다 (컴파일러가 기본생성자 안만들어준다 -> )
	// 생성자
	public PaginationDB(int currentPage, int listCount) {
		this.currentPage = currentPage; // 현재 페이지 <=== 동적 계산에 이것 필요
		this.listCount = listCount; // 전체 게시글 수 <=== 동적 계산에 이것 필요
		
		calculatePagination(); // 계산 메소드 호출
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
		calculatePagination(); // 계산 메소드 호출 (페이지 계산에 필요한 변수들 값이 변경되면 다시 페이지 계산 필요)
	}

	public int getListCount() {
		return listCount;
	}

	public void setListCount(int listCount) {
		this.listCount = listCount;
		calculatePagination(); // 계산 메소드 호출
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
		calculatePagination(); // 계산 메소드 호출
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		calculatePagination(); // 계산 메소드 호출
	}

	public int getMaxPage() {
		return maxPage;
	}

	public void setMaxPage(int maxPage) {
		this.maxPage = maxPage;
	}

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}

	public int getPrevPage() {
		return prevPage;
	}

	public void setPrevPage(int prevPage) {
		this.prevPage = prevPage;
	}

	public int getNextPage() {
		return nextPage;
	}

	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}

	@Override
	public String toString() {
		return "Pagination [currentPage=" + currentPage + ", listCount=" + listCount + ", limit=" + limit
				+ ", pageSize=" + pageSize + ", maxPage=" + maxPage + ", startPage=" + startPage + ", endPage="
				+ endPage + ", prevPage=" + prevPage + ", nextPage=" + nextPage + "]";
	}

	// 페이징 처리에 필요한 값을 계산하는 메소드 (클래스 내부에서만 사용하는 메소드)
	private void calculatePagination() {
		// 전체 게시글 수 : 500 | 보여지는 게시글 수 : 10개
		// -> 마지막 페이지 번호? 500/10 = 50 (page)
		// 전체 게시글 수 : 501 | 보여지는 게시글 수 : 10개
		// -> 마지막 페이지 번호? 501/10 = 51 (50.1 -> 올림처리) (int/int = int) 
		maxPage = (int)Math.ceil( (double)listCount / limit); //(int)Math.ceil( (double)int / int)
		
		// * startPage : 목록 하단에 노출된 페이지의 시작 번호
		//
		// 현재 페이지가 1  ~ 10 : 1
		// 현재 페이지가 11 ~ 20 : 11
		// 현재 페이지가 21 ~ 30 : 21
		startPage = (currentPage - 1)/pageSize * pageSize + 1; // 	ex) currentPage = 9, pageSize = 10
		         //           int    /   int    					ex) (9 - 1) / 10 * 10 + 1  = 8 / 10 * 10 + 1
		         //                 int        * int      + 1    	ex)        0     * 10 + 1  = 1 (startPage)
		
		// * endPage : 목록 하단에 노출된 페이지의 끝 번호
		//
		// 현재 페이지가 1  ~ 10 : 10
		// 현재 페이지가 11 ~ 20 : 20
		// 현재 페이지가 21 ~ 30 : 30		
		endPage = startPage + pageSize - 1;
		
		// 만약 endPage가 maxPage를 초과하는 경우
		if (endPage > maxPage) endPage =  maxPage;
		
		
		// ------------------------------------------------------
		//
		// * prevPage(<) : 목록하단에 노출된 번호의 이전 목록 끝번호
		// * nextPage(>) : 목록하단에 노출된 번호의 다음 목록 시작 번호
		
		// 현재 페이지가 1 ~ 10 인 경우 (case1)
		// < : 1 페이지
		// > : 11 페이지
		
		// 현재 페이지가 11 ~ 20 인 경우(case2)
		// < : 10 페이지
		// > : 21 페이지
		
		// 현재 페이지가 41 ~ 50 인 경우 (maxPage가 50) (case3)
		// < : 40 페이지
		// > : 50 페이지
		
		if(currentPage <= pageSize) prevPage = 1; // case1
		else prevPage = startPage - 1; // case2 & case3
		
		if(maxPage == endPage) { // case3
			nextPage = maxPage;
		} else {
			nextPage = endPage + 1; // case1&case2
		}
		
	}
}

