package edu.og.moa.common.utillity;

import java.text.SimpleDateFormat;

public class Util {
	
	
	// Cross Site Scripting(XSS) 방지 처리
	// - 권한이 없는 사용자가 사이트에 스크립트를 작성하는 것
	// - 웹 애플리케이션에서 발생하는 취약점
	
	public static String XSSHandling(String content) { // 제목또는 내용에 작성된 <script> 를 바꿔주기
		// 스크립트나 마크업 언어에서 기호나 기능을 나타내는 문자를 변경 처리
		// < - &lt;
		// > - &gt;
		// & - &amp;
		// " - &quot;
		
		
		content = content.replaceAll("&", "&amp;");// "&"와 "&lt"의 &구분을 위해 먼저 이걸 변환해야 함
		content = content.replaceAll("<", "&lt;"); // &lt;script>
		content = content.replaceAll(">", "&gt;"); // &lt;script&gt;
		content = content.replaceAll("\"", "&quot;"); // &lt;script&gt;
		
		return content;
		
	}
	
	// 파일명 변경 메소드
	public static String fileRename(String originFileName) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = sdf.format(new java.util.Date(System.currentTimeMillis()));

		int ranNum = (int) (Math.random() * 100000); // 5자리 랜덤 숫자 생성

		String str = "_" + String.format("%05d", ranNum); 

		String ext = originFileName.substring(originFileName.lastIndexOf("."));

		return date + str + ext;
	}		
	
}
