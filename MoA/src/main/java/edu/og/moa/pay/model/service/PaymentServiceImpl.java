package edu.og.moa.pay.model.service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.og.moa.pay.model.dao.PaymentMapper;
import edu.og.moa.pay.model.dto.Payment;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper mapper;

    @Value("${portone.api.key}")
    private String apiKey;

    @Value("${portone.api.secret}")
    private String apiSecret;

    @Override
    public int insertPayment(Payment payment) {
        System.out.println("결제 저장 요청: " + payment);
        return mapper.insertPayment(payment);
    }

    @Override
    public Payment selectPaymentByImpUid(String impUid) {
        return mapper.selectPaymentByImpUid(impUid);
    }

    /**
     * PortOne 결제 취소
     */
    @Override
    public boolean cancelPayment(String impUid, String reason) throws Exception {
        // Access Token 발급
        String token = getAccessToken();
        System.out.println("[PortOne Token 발급 성공] " + token);

        // 결제 취소 요청
        URL url = new URL("https://api.iamport.kr/payments/cancel");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", token);
        conn.setDoOutput(true);

        String body = String.format("{\"imp_uid\":\"%s\",\"reason\":\"%s\"}", impUid, reason);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes("utf-8"));
        }

        int responseCode = conn.getResponseCode();
        System.out.println("[PortOne 취소 응답 코드] " + responseCode);

        if (responseCode == 200) {
            // DB 상태 업데이트
            mapper.updatePaymentStatus(impUid, "C");
            System.out.println("[DB 업데이트 완료] impUid=" + impUid);
            return true;
        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) response.append(line);
            System.err.println("[PortOne 취소 실패] " + response);
            return false;
        }
    }

    /**
     * PortOne Access Token 발급
     */
    private String getAccessToken() throws Exception {
        URL url = new URL("https://api.iamport.kr/users/getToken");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String body = String.format("{\"imp_key\":\"%s\",\"imp_secret\":\"%s\"}", apiKey, apiSecret);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes("utf-8"));
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) response.append(line);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response.toString());
        return "Bearer " + node.get("response").get("access_token").asText();
    }
}
