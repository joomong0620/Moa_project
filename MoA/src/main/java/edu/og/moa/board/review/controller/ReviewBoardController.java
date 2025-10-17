package edu.og.moa.board.review.controller;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.og.moa.board.review.model.dto.ReviewBoard;
import edu.og.moa.board.review.model.dto.ReviewComment;
import edu.og.moa.board.review.model.dto.ReviewImage;
import edu.og.moa.board.review.model.service.ReviewBoardService;
import edu.og.moa.member.model.dto.Member;
import edu.og.moa.mypage.model.service.MyPageService;
import edu.og.moa.pay.model.dto.Payment;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;

@Controller
@RequestMapping("/reviewboard")
@SessionAttributes("loginMember")
public class ReviewBoardController {

    @Autowired
    private ReviewBoardService service;
    
    @Autowired
    private MyPageService myPageService;

    @Value("${my.reviewboard.location}")
    private String uploadDir;

    @Value("${my.reviewboard.webpath}")
    private String webPath;
    // 비동기 목록
    @GetMapping("/list")
    @ResponseBody
    public Map<String, Object> getReviewListAjax(
            @RequestParam("boardCode") int boardCode,
            @RequestParam(value = "cp", required = false, defaultValue = "1") int cp) {
        return service.selectReviewList(boardCode, cp);
    }

    // 리뷰 목록
    @GetMapping("/{boardCode:[0-9]+}")
    public String selectReviewList(
            @PathVariable("boardCode") int boardCode,
            @RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
            Model model,
            HttpSession session) {

        Map<String, Object> map = service.selectReviewList(boardCode, cp);
        model.addAttribute("map", map);
        model.addAttribute("boardCode", boardCode);

        return "board/reviewboard/reviewList";
    }
    // 리뷰 상세
    @GetMapping("/{boardCode:[0-9]+}/{reviewNo:[0-9]+}")
    public String selectReviewDetail(
            @PathVariable("boardCode") int boardCode,
            @PathVariable("reviewNo") int reviewNo,
            Model model,
            @SessionAttribute(value = "loginMember", required = false) Member loginMember,
            RedirectAttributes ra,
            HttpServletRequest req,
            HttpServletResponse resp,
            HttpSession session) throws ParseException {

        Map<String, Object> map = new HashMap<>();
        map.put("boardCode", boardCode);
        map.put("boardNo", reviewNo);

        ReviewBoard board = service.selectReviewDetail(map);
        String path;

        if (board != null) {
            board.setCommentList(service.selectCommentList(reviewNo));

            if (loginMember == null || loginMember.getMemberNo() != board.getMemberNo()) {
                Cookie c = null;
                Cookie[] cookies = req.getCookies();

                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if (cookie.getName().equals("readReviewNo")) {
                            c = cookie;
                            break;
                        }
                    }
                }

                int result = 0;
                if (c == null) {
                    c = new Cookie("readReviewNo", "|" + reviewNo + "|");
                    result = service.updateReviewReadCount(reviewNo);
                } else {
                    if (!c.getValue().contains("|" + reviewNo + "|")) {
                        c.setValue(c.getValue() + "|" + reviewNo + "|");
                        result = service.updateReviewReadCount(reviewNo);
                    }
                }

                if (result > 0) {
                    board.setBoardCount(board.getBoardCount() + 1);
                    c.setPath("/");
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, 1);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date current = new Date();
                    Date tmr = sdf.parse(sdf.format(new Date(cal.getTimeInMillis())));
                    long diff = (tmr.getTime() - current.getTime()) / 1000;
                    c.setMaxAge((int) diff);
                    resp.addCookie(c);
                }
            }

            boolean isLoggedIn = (loginMember != null);
            boolean isWriter = (isLoggedIn && loginMember.getMemberNo() == board.getMemberNo());

            model.addAttribute("isLoggedIn", isLoggedIn);
            model.addAttribute("isWriter", isWriter);
            
            model.addAttribute("board", board);
            path = "board/reviewboard/reviewDetail";

        } else {
            path = "redirect:/reviewboard/" + boardCode;
            ra.addFlashAttribute("message", "존재하지 않는 게시글입니다.");
        }

        return path;
    }

	// 리뷰 작성 페이지 이동
    @GetMapping("/write/{boardCode}")
    public String writeReviewPage(
            @PathVariable("boardCode") int boardCode, 
            Model model,
            @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        
        // 로그인 확인
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        
        // 예매 내역 조회
        List<Payment> paymentList = myPageService.selectPaymentList(loginMember.getMemberNo());
        
        model.addAttribute("boardCode", boardCode);
        model.addAttribute("reservationList", paymentList);
        
        return "board/reviewboard/reviewWrite";
    }

    // 리뷰 등록 (이미지 포함)
    @PostMapping("/write")
    public String insertReview(
            ReviewBoard board,
            @RequestParam("images") List<MultipartFile> imageFiles,
            @RequestParam("impUid") String impUid,
            RedirectAttributes ra,
            HttpSession session) throws IOException {

        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember == null) {
            loginMember = new Member();
            loginMember.setMemberNo(3);
            session.setAttribute("loginMember", loginMember);
        }

        board.setMemberNo(loginMember.getMemberNo());

        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        // 로그 출력 테스트
        System.out.println("========== [이미지 업로드 정보] ==========");
        System.out.println("실제 저장 경로 : " + uploadDir);
        System.out.println("웹 접근 경로   : " + webPath);
        System.out.println("업로드한 파일 수 : " + imageFiles.size());
        System.out.println("=====================================");

        List<ReviewImage> imageList = new ArrayList<>();

        for (int i = 0; i < imageFiles.size(); i++) {
            MultipartFile file = imageFiles.get(i);
            if (!file.isEmpty()) {
                String originalName = file.getOriginalFilename();

                // 괄호, 공백 등 제거
                String cleanName = originalName.replaceAll("[()\\s]", "_");

                // 오늘 날짜 (yyyyMMdd)
                String today = new SimpleDateFormat("yyyyMMdd").format(new Date());

                // 새로운 파일명: 20251017_images_3_.jpeg
                String rename = today + "_" + cleanName;

                // 실제 파일 저장
                file.transferTo(new File(uploadDir + rename));

                // 로그
                System.out.println("파일 저장 완료 → " + uploadDir + rename);

                ReviewImage img = new ReviewImage();
                img.setImgPath(webPath);
                img.setImgOrig(originalName);
                img.setImgRename(rename);
                img.setImgOrder(i); // 첫 번째(0)는 썸네일
                imageList.add(img);
            }
        }


        board.setImageList(imageList);

        int result = service.insertReviewBoard(board);

        if (result > 0) {
            ra.addFlashAttribute("message", "리뷰가 등록되었습니다.");
            return "redirect:/reviewboard/" + board.getCommunityCode();
        } else {
            ra.addFlashAttribute("message", "리뷰 등록 실패");
            return "redirect:/reviewboard/write/" + board.getCommunityCode();
        }
    }


    // 리뷰 수정 페이지 이동
    @GetMapping("/update/{reviewNo}")
    public String updateReviewPage(
            @PathVariable("reviewNo") int reviewNo,
            Model model,
            @SessionAttribute(value = "loginMember", required = false) Member loginMember,
            RedirectAttributes ra) {

        // 게시글 상세 조회
        Map<String, Object> map = new HashMap<>();
        map.put("boardNo", reviewNo);
        ReviewBoard board = service.selectReviewDetail(map);

        if (board == null) {
            ra.addFlashAttribute("message", "존재하지 않는 게시글입니다.");
            return "redirect:/reviewboard/2"; // 기본 게시판 코드로 이동
        }

        // 로그인 및 작성자 확인
        if (loginMember == null || board.getMemberNo() != loginMember.getMemberNo()) {
            ra.addFlashAttribute("message", "본인 글만 수정할 수 있습니다.");
            return "redirect:/reviewboard/" + board.getCommunityCode() + "/" + board.getBoardNo();
        }

        // 데이터 모델에 담아서 수정 페이지로 전달
        model.addAttribute("board", board);
        return "board/reviewboard/reviewUpdate";
    }

    
    @PostMapping("/update")
    public String updateReview(
            @ModelAttribute ReviewBoard board,
            @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles,
            @SessionAttribute(value = "loginMember", required = false) Member loginMember,
            RedirectAttributes ra) throws IOException {

        // 로그인 안 한 경우
        if (loginMember == null) {
            ra.addFlashAttribute("message", "로그인 후 이용해주세요.");
            return "redirect:/reviewboard/" + board.getCommunityCode() + "/" + board.getBoardNo();
        }

        // 본인 글이 아닌 경우
        ReviewBoard origin = service.selectReviewDetail(Map.of("boardNo", board.getBoardNo()));
        if (origin == null || origin.getMemberNo() != loginMember.getMemberNo()) {
            ra.addFlashAttribute("message", "본인 글만 수정할 수 있습니다.");
            return "redirect:/reviewboard/" + board.getCommunityCode() + "/" + board.getBoardNo();
        }

        // 수정 처리
        int result = service.updateReviewBoard(board);

	    // 별점 업데이트
        if (board.getStar() != null && board.getStar() > 0) {
            service.upsertStar(board.getBoardNo(), loginMember.getMemberNo(), String.valueOf(board.getStar()));
        }

        
     // 새로운 이미지가 있으면 추가
        if (imageFiles != null && !imageFiles.isEmpty()) {
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            for (int i = 0; i < imageFiles.size(); i++) {
                MultipartFile file = imageFiles.get(i);
                if (!file.isEmpty()) {
                	String originalName = file.getOriginalFilename();
                	String safeName = originalName.replaceAll("[()]", "_");
                	String rename = System.currentTimeMillis() + "_" + i + "_" + safeName;

                    ReviewImage img = new ReviewImage();
                    img.setBoardNo(board.getBoardNo());
                    img.setImgPath(webPath);
                    img.setImgOrig(originalName);
                    img.setImgRename(rename);
                    img.setImgOrder(i + 1);

                    service.insertReviewImage(img);
                }
            }
        }


        if (result > 0) {
            ra.addFlashAttribute("message", "리뷰가 수정되었습니다.");
        } else {
            ra.addFlashAttribute("message", "리뷰 수정에 실패했습니다.");
        }

        return "redirect:/reviewboard/" + board.getCommunityCode() + "/" + board.getBoardNo();
    }


    // 리뷰 삭제
    @PostMapping("/delete")
    public String deleteReview(
            @RequestParam("boardNo") int boardNo,
            @RequestParam("communityCode") int communityCode,
            RedirectAttributes ra) {

        int result = service.deleteReviewBoard(boardNo);

        if (result > 0)
            ra.addFlashAttribute("message", "리뷰가 삭제되었습니다.");
        else
            ra.addFlashAttribute("message", "리뷰 삭제 실패");

        return "redirect:/reviewboard/" + communityCode;
    }

    // 별점 등록/수정
    @PostMapping("/star")
    @ResponseBody
    public int upsertStar(@RequestBody Map<String, Object> map,
                          @SessionAttribute("loginMember") Member loginMember) {

        System.out.println("★ star : " + map);

        int boardNo = (int) map.get("boardNo");
        String starValue = String.valueOf(map.get("starValue"));

        System.out.println("boardNo=" + boardNo + ", starValue=" + starValue);
        
        return service.upsertStar(boardNo, loginMember.getMemberNo(), starValue);
    }

    // 평균 별점
    @GetMapping("/star/{boardNo}")
    @ResponseBody
    public Double getAverageStar(@PathVariable("boardNo") int boardNo) {
        return service.selectAverageStar(boardNo);
    }
}
