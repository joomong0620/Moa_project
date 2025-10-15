package edu.og.moa.pay.model.dao;

import org.apache.ibatis.annotations.Mapper;
import edu.og.moa.pay.model.dto.Payment;

@Mapper
public interface PaymentMapper {

    int insertPayment(Payment payment);

    // 결제
    Payment selectPaymentByImpUid(String impUid);

    // 결제 취소
	int deletePaymentByImpUid(String impUid, int memberNo);
}
