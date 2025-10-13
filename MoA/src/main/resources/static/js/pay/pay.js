console.log("pay.js loaded");

// 총 금액 계산
const seatSelect = document.getElementById("seatSelect");
const totalPrice = document.getElementById("totalPrice");
const itemPrice = document.getElementById("itemPrice");

seatSelect.addEventListener("change", () => {
  const price = seatSelect.options[seatSelect.selectedIndex].dataset.price;
  const formatted = price ? parseInt(price).toLocaleString() : 0;
  totalPrice.textContent = formatted + "원";
  itemPrice.textContent = formatted + "원";
});

// 결제 버튼 클릭 시 PortOne 호출
document.getElementById("payNowBtn").addEventListener("click", () => {
  const selected = seatSelect.options[seatSelect.selectedIndex];
  const price = selected.dataset.price ? parseInt(selected.dataset.price) : 0;
  if (!price) return alert("좌석을 선택하세요.");

  if (
    !document.getElementById("agreeTerms").checked ||
    !document.getElementById("agreePrivacy").checked
  ) {
    return alert("약관 및 개인정보 수집에 동의해주세요.");
  }

  requestPay(price);
});

// 결제 요청
function requestPay(amount) {
  const IMP = window.IMP;
  IMP.init("imp80522717"); // 테스트용 가맹점코드

  IMP.request_pay(
    {
      pg: "html5_inicis",
      pay_method: "card",
      merchant_uid: "order_" + new Date().getTime(),
      name: document.getElementById("showName")?.innerText || "테스트 결제",
      amount: amount,
      buyer_email: document.getElementById("buyerEmail").innerText,
      buyer_name: document.getElementById("buyerName").innerText,
      buyer_tel: document.getElementById("buyerPhone").innerText,
    },
    (rsp) => {
      if (rsp.success) {
        alert("결제가 완료되었습니다!");

        fetch("/payment/complete", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            impUid: rsp.imp_uid,
            merchantUid: rsp.merchant_uid,
            payMuch: rsp.paid_amount,
            payWhat: rsp.pay_method,
            payOk: "Y",
            boardNo: 101, // 테스트용
            memberNo: 3,
          }),
        })
          .then((res) => res.json())
          .then((data) => {
            if (data.result > 0) {
              // 결제 성공 시 이동
              window.location.href = "/payment/success";
            } else {
              alert("서버 저장 실패");
            }
          });
      }
    }
  );
}
