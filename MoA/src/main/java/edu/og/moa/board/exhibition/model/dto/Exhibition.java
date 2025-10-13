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
public class Exhibition { // 일단은 JsonExhibtion과 Exhibition을 같게 두고쓰자 (프론트에서 화면만들기 편리하게)
	
	// 0) DTO for our purpose
	private int exhibitNo; // BOARD_No, boardNo (게시판 글번호; PK)
	private String exhibitCreateDate; //			collectDate ("COLLECTED_DATE")			"B_CREATE_DATE"				==> NOT-NULL	
	private int readCount; // 게시글 조회수
	//private char delFlag; // 게시글 삭제 flag
	private String delFlag; // 게시글 삭제 flag	
	private int memberNo; // FK ??
	//private int boardCode;	// FK ?? 

	// 1) BOARDTYPE JOIN or given from @PathVarialbe?
	private int communityCode; // FK ?? 
	
	// 2) 서브쿼리
	private int likeCount; // 좋아요 수 (대응되는 DB 컬럼은 없으나 -> mapper resultMap에서는 있어야 Exhibition DTO와 대응된다)
	
	// 3) 회원 JOIN
	private String memberNickname; 
	//private int memberNo;
	private String profileImage;
	private String thumbnail;

	// 이미지 목록
	private List<BoardImgDB> imageList;  // From DB, so BoardImageDB, not JsonBoardImage
	
	// Author 목록
	//private List<AuthorDB> authorList; 

	// 4) EXHIBITION JOIN
	// DTO 필드									 	// API DTO 필드(JSON-key)				// ORACLE DATABASE FIELD	
	private String exhibitTitle; // 게시판 타이틀		title ("TITLE")							"BOARD_TITLE" 				==> NOT-NULL
	/////////////////////////////////////////////////////////// 여기까지가 전시게시글 목록 조회에 필요한 정보들이고 /////////////////////////////////////////
	/////////////////////////////////////////////////////////// 이 아래가 특정 게시글 상세 조회에 추가로 필요한 정보들이다	///////////////////////////////////
	private String exhibitContent; // 게시판 글내용	description ("DESCRIPTION")				"BOARD_CONTENT" 			==> NOT-NULL (imputation needed)
	private String exhibitUpdateDate; 	//			추후 수정시 SYSDATE						"B_UPDATE_DATE"				==> NOT-NULL	
	private String exhibitImgObject; // 			imageObject	("IMAGE_OBJECT")			"IMG_PATH", "IMG_ORIG" (IMAGE_OBJECT = IMG_PATH/IMG_ORIG)
	
	private String exhibitSubTitle; // 				subDescription ("SUB_DESCRIPTION")		"EXHIBIT_SUB_TITLE"
	private String exhibitDate; // 전시기간			period ("PERIOD") 						"EXHIBIT_DATE"
	private String exhibitLocation; // 				eventSite ("EVENT_SITE")				"EXHIBIT_SITE" //("EXHIBIT_LOCATION") 
	
	// GENRE 테이블 JOIN
	private String exhibitGenre; // 				genre ("GENRE")							exhibitGenre("GENRE_NAME")는 EXHIBITION DB에서 genreNo보고 GENRE DB로부터 JOIN 조회해온다(DB간 관계는 ERD봐라)
	private String exhibitContact; // 				contactPoint ("CONTACT_POINT")			"EXHIBIT_CONTACT"			==> NOT-NULL (imputation needed) 
	private String exhibitAudience; // 				audience ("AUDIENCE")					"EXHIBIT_AUDIENCE"
	private String exhibitCharge; // 				charge ("CHARGE")						"EXHIBIT_CHARGE"			==> NOT-NULL (imputation needed)
	
	// AUTHOR 테이블 JOIN
	private String exhibitAuthor; // 				author ("AUTHOR")						LISTAGG로 이미 AUTHOR_NAME을 가져온다 (AUTHOR DB JOIN대신) //exhibitAuthor("AUTHOR_NAME")는 EXHIBITION DB에서 exhibitNo(=boardNo)보고 AUTHOR DB로부터 JOIN 조회해서 String으로 합치기 (DB간 관계는 ERD봐라)//("AUTHOR_NAME")
	
	// INSTITUTION 테이블 조회
	private String exhibitInstitution;  // 			institutionName ("CNTC_INSTT_NM")		exhibitInstitution("EXHIBIT_INST_NAME")은 EXHIBITION DB에서 institutionNo보고 INSTITUTUION DB로부터 조회해온다(DB간 관계는 ERD봐라) ==> NOT-NULL
										//													("EXHIBIT_INST_TEL" -> 22개 기관에 문의전화번호 수동수집한 정보: exhibitContact가 null일때 디폴트값으로 써라) 	==> NOT-NULL
	 
	// CONTRIBUTOR 테이블 JOIN
	private String exhibitContributor; // 			contributor ("CONTRIBUTOR")				exhibitContributor("HOST_SUPPORT") EXHIBITION DB에서 exhibitNo(=boardNo)로 CONTRIBUTOR DB JOIN 조회해서"EXHIBIT_HOST" and "EXHIBIT_SUPPORT" String으로 합치기 (DB간 관계는 ERD봐라) 
}

