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
public class JsonExhibition {
	
	// 0) DTO for our purpose
	private int exhibitNo; // BOARD_No, boardNo (게시판 글번호; PK)
	private String exhibitUpdateDate;
	private int readCount; // 게시글 조회수
	//private char delFlag; // 게시글 삭제 flag
	//private int memberNo; // FK ??
	// private int boardCode;	// FK ?? 

	// 1) BOARDTYPE JOIN
	private int communityCode;
	
	// 2) 서브쿼리
	private int likeCount; // 좋아요 수
	
	// 3) 회원 JOIN
	private String memberNickname; 
	private int memberNo;
	private String profileImage;
	private String thumbnail;

	// 이미지 목록
	private List<JsonBoardImage> imageList;
	
	
	// 4) EXHIBITION JOIN
	// DTO 필드									 	// API DTO 필드(JSON-key)				// ORACLE DATABASE FIELD	
	private String exhibitTitle; // 게시판 타이틀		title ("TITLE")							"BOARD_TITLE" 				==> NOT-NULL
	private String exhibitCreateDate; //			collectDate ("COLLECTED_DATE")			"B_CREATE_DATE"				==> NOT-NULL
	private String exhibitContent; // 게시판 글내용	description ("DESCRIPTION")				"BOARD_CONTENT" 			==> NOT-NULL (imputation needed)
	
	private String exhibitImgObject; // 			imageObject	("IMAGE_OBJECT")			"IMG_PATH", "IMG_ORIG" (IMAGE_OBJECT = IMG_PATH/IMG_ORIG)
	
	private String exhibitSubTitle; // 				subDescription ("SUB_DESCRIPTION")		"EXHIBIT_SUB_TITLE"
	private String exhibitDate; // 					period ("PERIOD") 						"EXHIBIT_DATE"
	private String exhibitLocation; // 				eventSite ("EVENT_SITE")				"EXHIBIT_LOCATION"
	private String exhibitGenre; // 				genre ("GENRE")							"EXHIBIT_GENRE"
	private String exhibitContact; // 				contactPoint ("CONTACT_POINT")			"EXHIBIT_CONTACT"			==> NOT-NULL (imputation needed) 
	private String exhibitAudience; // 				audience ("AUDIENCE")					"EXHIBIT_AUDIENCE"
	private String exhibitCharge; // 				charge ("CHARGE")						"EXHIBIT_CHARGE"			==> NOT-NULL (imputation needed)
	
	// AUTHOR 테이블 JOIN
	private String exhibitAuthor; // 				author ("AUTHOR")						"AUTHOR_NAME"
	
	// INSTITUTION 테이블 JOIN
	private String exhibitInstitution;  // 			institutionName ("CNTC_INSTT_NM")		"EXHIBIT_INST_NAME", 		==> NOT-NULL
										//													("EXHIBIT_INST_TEL" -> 22개 기관에 문의전화번호 수동수집) 	==> NOT-NULL
	 
	// CONTRIBUTOR 테이블 JOIN
	private String exhibitContributor; // 			contributor ("CONTRIBUTOR")				"EXHIBIT_HOST" and "EXHIBIT_SUPPORT"
}

