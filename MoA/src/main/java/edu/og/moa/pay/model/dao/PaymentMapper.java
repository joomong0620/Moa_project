package edu.og.moa.pay.model.dao;

import org.apache.ibatis.annotations.Mapper;
import edu.og.moa.pay.model.dto.Payment;

@Mapper
public interface PaymentMapper {

    int insertPayment(Payment payment);

    Payment selectPaymentByImpUid(String impUid);
}
