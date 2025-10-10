package edu.og.moa.board.exhibition.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ExhibitionItem {
	
	// 0) DTO for our purpose						// "JsonExhibition" 대응 DTO 필드
	private int boardNo;							// exhibitNo; // BOARD_No, boardNo (게시판 글번호; PK)
	private String bUpdateDate;						// exhibitUpdateDate;
								// => TO_DATE()으로
	private int boardCount;							// readCount; // 게시글 조회수

	// 1) BOARDTYPE JOIN
	private int communityCode; 	//					// communityCode;
								// BOARDTYPE 테이블에서 WHERE조건? (최상위 부모 테이블: BOARDTYPE => 독립생성)
								// 우리경우 preset 모두 5개 게시판 알고 있으니, 그냥 데이터베이스에 직접 넣는걸로

	// 2) 서브쿼리
	private int likeCount; 	// 좋아요 수				// 한 boardNo 에 좋아요 클릭한 memberNo수로 센다: 예시) SELECT COUNT(MEMBER_NO) FROM LIKE WHERE BOARD_NO = 10;
							// LIKE 테이블 SELECT COUNT() ?
	// 3) 회원 JOIN
	private String memberNickname; 					// memberNickname; 
	private int memberNo;							// memberNo;
	private String profileImg;						// profileImage;
	private String thumbnail; 	//					// thumbnail; // imageList에서 imageOrder = 0인 이미지(우리경우 포스터): imageList[0]
								// (<== "exhibitImgObject"이 null이면 기본이미지(monet_pond.jpg), 아니면 "exhibitImgObject"값)
								// leave as a backup (실제 넣을 DB 필드는  BOARD_IMG테이블에 IMG_ORDER=0경우 
	// 이미지 목록
	private List<BoardImgDB> imageList;				// List<JsonBoardImage> imageList;
								// <= imageList[0] = thumbnail
	
	// 4) EXHIBITION JOIN
	// DTO 필드									 	// "JsonExhibition" 대응 DTO 필드			// API DTO 필드(JSON-key)				// ORACLE DATABASE FIELD	
	private String boardTitle; 	// 게시판 타이틀		// exhibitTitle; // 게시판 타이틀			title ("TITLE")							"BOARD_TITLE" 				==> NOT-NULL
	private String bCreateDate; 					// exhibitCreateDate; 				collectDate ("COLLECTED_DATE")				"B_CREATE_DATE"				==> NOT-NULL
								// => TO_DATE()으로
	private String boardContent;// 게시판 글내용		// exhibitContent; // 게시판 글내용		description ("DESCRIPTION")				"BOARD_CONTENT" 			==> NOT-NULL (imputation needed)
	
	// leave as a backup (실제 넣을 DB 필드는  BOARD_IMG테이블에 IMG_ORDER=0경우 
	private String exhibitImgObject;//	 			// exhibitImgObject; 					imageObject	("IMAGE_OBJECT")			"IMG_PATH", "IMG_ORIG" (IMAGE_OBJECT = IMG_PATH/IMG_ORIG)
									// => thumbnail과 imageList[0]에 들어간다.
	
	private String exhibitSubTitle; // 				// exhibitSubTitle;  					subDescription ("SUB_DESCRIPTION")		"EXHIBIT_SUB_TITLE"
	private String exhibitDate; 	// 				// exhibitDate; 						period ("PERIOD") 						"EXHIBIT_DATE"
	//private String exhibitGenre;	//		 		// exhibitGenre; 	 					genre ("GENRE")							"EXHIBIT_GENRE"
	private GenreDB exhibitGenre;	//		 			// exhibitGenre; 	 					genre ("GENRE")							"EXHIBIT_GENRE"
							// <= GENRE 테이블에서 GENRE_NAME으로 GENRE_NO찾아 genreNo값 삽입 
							// 우리경우 preset 모두 10개 장르 알고 있으니, 그냥 데이터베이스에 직접 넣는걸로
							// (최상위 부모 테이블: BOARDTYPE => 독립생성)
	private String exhibitContact; // 				// exhibitContact; 		 				contactPoint ("CONTACT_POINT")			"EXHIBIT_CONTACT"			==> NOT-NULL (imputation needed) 
	private String exhibitAudience; // 				// exhibitAudience; 					audience ("AUDIENCE")					"EXHIBIT_AUDIENCE"
	private int	 exhibitCharge; 	// 				// exhibitCharge; 						charge ("CHARGE")						"EXHIBIT_CHARGE"			==> NOT-NULL (imputation needed)
									// <= "exhibitCharge" parsing해서 입장료를 숫자로 ( >= 0) 변환해서 대입
	// AUTHOR 테이블 JOIN
	//private String authorName;	// 				// exhibitAuthor; 						author ("AUTHOR")						"AUTHOR_NAME"
	private List<AuthorDB> authorList; //	 		// exhibitAuthor; 						author ("AUTHOR")						"AUTHOR_NAME"
									// <= AUTHOR 테이블 (다수의 authors일 경우: authorName1, authorName2, authorName3, 그외 몇명으로 " exhibitAuthor" 맨앞 3명까지 이름 입력)
									// SELECT: boardNo로 JOIN 테이블
									// INSERT/UPDATE/DELETE: 조회해서 있으면(조회 result >0)이면 done, 아니면(조회 result=0) INSERT/UPDATE/DELETE 수행
								
	// INSTITUTION 테이블 JOIN
	//private String exhibitInstName; // 			// exhibitInstitution;  				institutionName ("CNTC_INSTT_NM")		"EXHIBIT_INST_NAME", 		==> NOT-NULL
									// => INSTITUTION 테이블																			("EXHIBIT_INST_TEL" -> 22개 기관에 문의전화번호 수동수집) 	==> NOT-NULL
									// SELECT: boardNo로 JOIN 테이블
									// INSERT/UPDATE/DELETE: 조회해서 있으면(조회 result >0)이면 done, 아니면(조회 result=0) INSERT/UPDATE/DELETE 수행
	//private String exhibitLocation; // 			// exhibitLocation; 					eventSite ("EVENT_SITE")				"EXHIBIT_LOCATION"
									// => INSTITUTION 테이블에서 JOIN으로 얻어오는 값? (최상위 부모 테이블: INSTITUTION => 독립생성)
									// => 위 exhibitInstName에 따라 처리 
									// SELECT: boardNo로 JOIN 테이블
									// INSERT/UPDATE/DELETE: 조회해서 있으면(조회 result >0)이면 done, 아니면(조회 result=0) INSERT/UPDATE/DELETE 수행
	 								// 우리경우 20개 알고 있으니, 그냥 데이터베이스에 직접 넣는걸로
									// (최상위 부모 테이블: INSTITUTION => 독립생성)
	private InstitutionDB exhibitInstName;
	
	// CONTRIBUTOR 테이블 JOIN
	//private String exhibitContributor;// exhibitContributor;  							contributor ("CONTRIBUTOR")				"EXHIBIT_HOST" and "EXHIBIT_SUPPORT"
	private ContributorDB exhibitContributor;// exhibitContributor; 							contributor ("CONTRIBUTOR")				"EXHIBIT_HOST" and "EXHIBIT_SUPPORT"
										// => CONTRIBUTOR 테이블 (EXHIBIT_HOST 와 EXHIBIT_SUPPORT로 나눈다)

}

