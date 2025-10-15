package edu.og.moa.pay.model.service;

import edu.og.moa.pay.model.dto.Payment;

public interface PaymentService {

    /** 결제 정보 */
    int insertPayment(Payment payment);

    /** PortOne imp_uid로 결제 조회 */
    Payment selectPaymentByImpUid(String impUid);

    /** 결제 취소 */
    boolean cancelPayment(String impUid, String reason) throws Exception;

   
}
