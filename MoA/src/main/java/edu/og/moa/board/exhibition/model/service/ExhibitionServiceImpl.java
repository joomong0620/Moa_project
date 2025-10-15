package edu.og.moa.board.exhibition.model.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.og.moa.board.exhibition.controller.ExhibitionController;
import edu.og.moa.board.exhibition.model.dao.ExhibitionMapper;
import edu.og.moa.board.exhibition.model.dto.AuthorDB;
import edu.og.moa.board.exhibition.model.dto.BoardDB;
import edu.og.moa.board.exhibition.model.dto.BoardImgDB;
import edu.og.moa.board.exhibition.model.dto.EventPeriod;
import edu.og.moa.board.exhibition.model.dto.Exhibition;
import edu.og.moa.board.exhibition.model.dto.JsonBoardImage;
import edu.og.moa.board.exhibition.model.dto.PaginationDB;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExhibitionServiceImpl implements ExhibitionService{

	@Autowired
	private ExhibitionMapper mapper;
	
	// 전시게시글 목록조회
	@Override
	public Map<String, Object> selectExhibitionList(int communityCode, int cp) {
		// 1. 특정 게시판의 삭제되지 않은 게시글 수 조회
		int listCount = mapper.getListCount(communityCode);
		
		// 2. 1번의 조회 결과 + cp를 이용해서 Pagination 객체 생성
		PaginationDB pagination = new PaginationDB(cp, listCount);
		
		// 3. 특정 게시판에서 현재 페이지에 해당하는 부분에 대한 게시글 목록 조회	
		// 1) offset 계산
		int offset = (pagination.getCurrentPage() - 1) * pagination.getLimit();
		
		// 2) RowBounds 객체 생성
		RowBounds rowBounds = new RowBounds(offset, pagination.getLimit());
		
		// 3) DB 목록 조회
		List<Exhibition> exhibitionList = mapper.selectExhibitionList(communityCode, rowBounds); 
		
		// 4) pagination, boardList를 Map에 담아서 반환
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pagination", pagination);
		map.put("exhibitionList", exhibitionList);
		// add more info. for thymeleaf 랜더링, 2025/10/14
		map.put("totalListCount", listCount);
		  
		// 5) 오늘 기준으로 exhibitionList에서 (현재세팅 pagination.getLimit()=10 건) 현재 진행중, 예정, 지난 전시로 그룹핑한다.
		// 날짜 표기법 변경 객체
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // java.util.SimpleDateFormat
    	 
		// String timeToCompare = "2025-10-13"; // 특정날짜를 지정하고 싶을때...
		String timeToCompare = null;
		if (timeToCompare == null) {
			// java.util.Date
			Date curTime = new Date(); // [요일] [월] [일] [시:분:초] [타임존] [연도]형식, 예시) Tue Oct 14 15:52:30 KST 2025 (내부적으로는 long millis -> 밀리초 단위 timestamp)
	        timeToCompare = sdf.format(curTime);  // String 객체
		} 
		
		List<EventPeriod> eventPeriodList = new ArrayList<>();
		for (int i=0; i < exhibitionList.size(); i++ ) {
			EventPeriod eventPeriod = new EventPeriod();
			eventPeriod.setExhibitNo(exhibitionList.get(i).getExhibitNo()); // Exhibition.exhibitNo = BoardDB.boardNo
			eventPeriod.setEventPeriod(exhibitionList.get(i).getExhibitDate()); 
			eventPeriodList.add(eventPeriod);
		}
		
		// eventPeriodList의 각 item에 상태 지정
		EventPeriodSorter.applyStatusToItems(eventPeriodList, timeToCompare); // 각 item.eventStatus에 current, future, past 남긴다.
		
        if (eventPeriodList.size() != exhibitionList.size()) {
        	System.out.println("[ERROR]:  exhibitionList or eventPeriodList to be checked"); // 
        	System.exit(0);  // 정상 종료
        }
        
        List<Exhibition> exhibitionListCurrent = new ArrayList<>();
        List<Exhibition> exhibitionListFuture = new ArrayList<>();
        List<Exhibition> exhibitionListPast = new ArrayList<>();
        int cntNull = 0;
        int cntCurrent = 0;
        int cntFuture = 0;
        int cntPast = 0;
        for (int i = 0; i < eventPeriodList.size(); i++) {
            EventPeriod eventPeriod = eventPeriodList.get(i);
            Exhibition exhibition = exhibitionList.get(i);
            if (eventPeriod.getEventStatus().equals("pastEvent")) { // pastEvent, futureEvent, currentEvent
            	cntPast++;
            	exhibitionListPast.add(exhibition);
            } else if (eventPeriod.getEventStatus().equals("futureEvent")) {
            	cntFuture++;
            	exhibitionListFuture.add(exhibition);
            } else if (eventPeriod.getEventStatus().equals("currentEvent")) {
            	cntCurrent++;
            	exhibitionListCurrent.add(exhibition);
            } else if (eventPeriod.getEventStatus().equals("nullPeriod")) {
            	System.out.println("ExhibitNo = " + exhibition.getExhibitNo() + " ; ExhibitDate(): " + exhibition.getExhibitDate());
            	System.out.println("eventPeriod.getEventStatus(): " + eventPeriod.getEventStatus());
            	cntNull++;
            	continue; // Exhibition.exhibitDate == null 인 경우는 그냥 skip (monkey patch.. temporarily)
            } else {
            	System.out.println("eventPeriod.getEventStatus(): " + eventPeriod.getEventStatus());
            	System.out.println("[ERROR] some serious issue to be address ");
            	System.exit(0);  // 정상 종료
            }
        }		
        
		log.info("exhibitionList.size() : '{}'", exhibitionList.size());
		log.info("# of null-period : '{}'", cntNull);
		log.info("exhibitionListCurrent.size() : '{}'", exhibitionListCurrent.size());
		log.info("exhibitionListFuture.size() : '{}'", exhibitionListFuture.size());
		log.info("exhibitionListPast.size() : '{}'", exhibitionListPast.size());
        // 카드목록 조회위한 exhibitionList를 그룹핑
		map.put("exhibitionListCurrent", exhibitionListCurrent);
		map.put("exhibitionListFuture", exhibitionListFuture);
		map.put("exhibitionListPast", exhibitionListPast);
		
		Map<String, Object> eventPeriodCount = new HashMap<>();
		eventPeriodCount.put("cntNull", cntNull);
		eventPeriodCount.put("cntCurrent", cntCurrent);
		eventPeriodCount.put("cntFuture", cntFuture);
		eventPeriodCount.put("cntPast", cntPast);
		eventPeriodCount.put("paginationLimit", pagination.getLimit());		
		map.put("eventPeriodCount", eventPeriodCount);		

		return map;
	}

	

	// 전시게시글 상세조회
	@Override
	public Exhibition selectExhibition(Map<String, Object> map) {
		// 
		return mapper.selectExhibition(map);
	}

	
//	// DB 이미지 조회 
//	@Override
//	public List<BoardImgDB> selectImageList() { // Exhibition DTO select시에 그안에서 implicit하게 수행되므로 여기서 선언하면 안됨
//		return mapper.selectImageList();
//	}	
//
//	// AUTHOR DB 조회
//	@Override
//	public List<AuthorDB> selectAuthorList() { // Exhibition DTO select시에 그안에서 implicit하게 수행되므로 여기서 선언하면 안됨
//		return mapper.selectAuthorList();
//	}	
//		
	// DB 이미지(파일) 목록 조회
	@Override
	public List<String> selectImageListAll() {
		// 
		return mapper.selectImageListAll();
	}

	// DB 이미지 조회
	@Override
	public List<BoardImgDB> selectImageListIndep() {
		return mapper.selectImageListIndep();
	}	

	// AUTHOR DB 조회
	@Override
	public List<AuthorDB> selectAuthorListIndep() {
		return mapper.selectAuthorListIndep();
	}


	// 좋아요 여부 확인
	@Override
	public int exhibitionLikeCheck(Map<String, Object> map) {
		// 
		return mapper.exhibitionLikeCheck(map);
	}


	// 조회수 증가
	@Override
	public int updateReadCount(int boardNo) {
		// 
		return mapper.updateReadCount(boardNo);
	}	
			
	// 검색용 전시게시글 목록조회
	@Override
	public Map<String, Object> selectExhibitionList(Map<String, Object> paramMap, int cp) {
		// 1. 특정 게시판의 삭제되지 않고 검색 조건이 일치하는 게시글 수 조회
		log.info("getSearchListCount() 입력 paramMap: {}", paramMap);
		
		log.info("getSearchListCount() 입력 paramMap.get('query'): '{}'", paramMap.get("query"));
		int listCount = mapper.getSearchListCount(paramMap);
		
		log.info("getSearchListCount() 성공한 행의 갯수: {}", listCount);
		// 2. 1번의 조회 결과 + cp를 이용해서 Pagination 객체 생성
		PaginationDB pagination = new PaginationDB(cp, listCount);
		
		// 3. 특정 게시판에서 현재 페이지에 해당하는 부분에 대한 게시글 목록 조회
		int offset = (pagination.getCurrentPage() - 1) * pagination.getLimit();
		
		RowBounds rowBounds = new RowBounds(offset, pagination.getLimit());
		
		
		List<Exhibition> exhibitionList = mapper.selectSearchExhibitionList(paramMap, rowBounds);
		
		// 4) pagination, boardList를 Map에 담아서 반환
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pagination", pagination);
		map.put("exhibitionList", exhibitionList);
		// add more info. for thymeleaf 랜더링, 2025/10/14
		map.put("totalListCount", listCount);

		
		// 5) 오늘 기준으로 exhibitionList에서 (현재세팅 pagination.getLimit()=10 건) 현재 진행중, 예정, 지난 전시로 그룹핑한다.
		// 날짜 표기법 변경 객체
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // java.util.SimpleDateFormat
    	 
		// String timeToCompare = "2025-10-13"; // 특정날짜를 지정하고 싶을때...
		String timeToCompare = null;
		if (timeToCompare == null) {
			Date curTime = new Date(); // [요일] [월] [일] [시:분:초] [타임존] [연도]형식, 예시) Tue Oct 14 15:52:30 KST 2025 (내부적으로는 long millis -> 밀리초 단위 timestamp)
	        timeToCompare = sdf.format(curTime);  // String 객체
		} 
		
		List<EventPeriod> eventPeriodList = new ArrayList<>();
		for (int i=0; i < exhibitionList.size(); i++ ) {
			EventPeriod eventPeriod = new EventPeriod();
			eventPeriod.setExhibitNo(exhibitionList.get(i).getExhibitNo()); // Exhibition.exhibitNo = BoardDB.boardNo
			eventPeriod.setEventPeriod(exhibitionList.get(i).getExhibitDate()); 
			eventPeriodList.add(eventPeriod);
		}
		
		// eventPeriodList의 각 item에 상태 지정
		EventPeriodSorter.applyStatusToItems(eventPeriodList, timeToCompare); // 각 item.eventStatus에 current, future, past 남긴다.
		
        if (eventPeriodList.size() != exhibitionList.size()) {
        	System.out.println("[ERROR]:  exhibitionList or eventPeriodList to be checked"); // 
        	System.exit(0);  // 정상 종료
        }
        
        List<Exhibition> exhibitionListCurrent = new ArrayList<>();
        List<Exhibition> exhibitionListFuture = new ArrayList<>();
        List<Exhibition> exhibitionListPast = new ArrayList<>();
        int cntNull = 0;
        int cntCurrent = 0;
        int cntFuture = 0;
        int cntPast = 0;
        for (int i = 0; i < eventPeriodList.size(); i++) {
            EventPeriod eventPeriod = eventPeriodList.get(i);
            Exhibition exhibition = exhibitionList.get(i);
            if (eventPeriod.getEventStatus().equals("pastEvent")) { // pastEvent, futureEvent, currentEvent
            	cntPast++;
            	exhibitionListPast.add(exhibition);
            } else if (eventPeriod.getEventStatus().equals("futureEvent")) {
            	cntFuture++;
            	exhibitionListFuture.add(exhibition);
            } else if (eventPeriod.getEventStatus().equals("currentEvent")) {
            	cntCurrent++;
            	exhibitionListCurrent.add(exhibition);
            } else if (eventPeriod.getEventStatus().equals("nullPeriod")) {
            	System.out.println("ExhibitNo = " + exhibition.getExhibitNo() + " ; ExhibitDate(): " + exhibition.getExhibitDate());
            	System.out.println("eventPeriod.getEventStatus(): " + eventPeriod.getEventStatus());
            	cntNull++;
            	continue; // Exhibition.exhibitDate == null 인 경우는 그냥 skip (monkey patch.. temporarily)
            } else {
            	System.out.println("eventPeriod.getEventStatus(): " + eventPeriod.getEventStatus());
            	System.out.println("[ERROR] unknown serious error ");
            	System.exit(0);  // 정상 종료
            }
        }		
        
		log.info("Search: exhibitionList.size() : '{}'", exhibitionList.size());
		log.info("Search: # of null-period : '{}'", cntNull);
		log.info("Search: exhibitionListCurrent.size() : '{}'", exhibitionListCurrent.size());
		log.info("Search: exhibitionListFuture.size() : '{}'", exhibitionListFuture.size());
		log.info("Search: exhibitionListPast.size() : '{}'", exhibitionListPast.size());
        // 카드목록 조회위한 exhibitionList를 그룹핑
		map.put("exhibitionListCurrent", exhibitionListCurrent);
		map.put("exhibitionListFuture", exhibitionListFuture);
		map.put("exhibitionListPast", exhibitionListPast);
		
		Map<String, Object> eventPeriodCount = new HashMap<>();
		eventPeriodCount.put("cntNull", cntNull);
		eventPeriodCount.put("cntCurrent", cntCurrent);
		eventPeriodCount.put("cntFuture", cntFuture);
		eventPeriodCount.put("cntPast", cntPast);
		eventPeriodCount.put("paginationLimit", pagination.getLimit());		
		map.put("eventPeriodCount", eventPeriodCount);		
	
		return map;
	}

	
}
