package edu.og.moa.pay.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.og.moa.pay.model.dto.Payment;
import edu.og.moa.pay.model.dao.PaymentMapper;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper mapper;

    @Override
    public int insertPayment(Payment payment) {
        System.out.println("결제 저장 요청: " + payment);
        return mapper.insertPayment(payment);
    }

    @Override
    public Payment selectPaymentByImpUid(String impUid) {
        return mapper.selectPaymentByImpUid(impUid);
    }
}
