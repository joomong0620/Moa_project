console.log("pay.js loaded");

const seatSelect = document.getElementById("seatSelect");
const totalPrice = document.getElementById("totalPrice");
const itemPrice = document.getElementById("itemPrice");

// 서버에서 전달된 데이터
const eventType = document.getElementById("eventType")?.value || "performance";
const boardNo = document.getElementById("boardNo")?.value;
const memberNo = document.getElementById("memberNo")?.value;

// 무료 전시일 경우
if (eventType === "exhibition") {
  const freeOption = [...seatSelect.options].find((opt) => opt.value === "무료");
  if (freeOption) {
    freeOption.selected = true;
    totalPrice.textContent = "0원";
    itemPrice.textContent = "0원";
  }
}

// 좌석 변경 시 금액 표시
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
  const eventTypeNow = document.getElementById("eventType")?.value || "performance";

  // 공연만 좌석 선택 필수
  if (eventTypeNow === "performance" && (!selected.value || selected.value === "")) {
    return alert("좌석을 선택하세요.");
  }

  // 약관 동의 체크
  if (
    !document.getElementById("agreeTerms").checked ||
    !document.getElementById("agreePrivacy").checked
  ) {
    return alert("약관 및 개인정보 수집에 동의해주세요.");
  }

  requestPay(price, eventTypeNow);
});

// 결제 요청
function requestPay(amount, eventType) {
  const IMP = window.IMP;
  IMP.init("imp80522717"); // PortOne 테스트용 가맹점 코드

  // 결제창 호출은 일단 100원
  const payAmount = eventType === "exhibition" && amount === 0 ? 100 : amount;

  IMP.request_pay(
    {
      pg: "html5_inicis",
      pay_method: "card",
      merchant_uid: "order_" + new Date().getTime(),
      name:
        document.getElementById("showName")?.innerText ||
        (eventType === "exhibition" ? "무료 전시 예약" : "테스트 결제"),
      amount: payAmount,
      buyer_email: document.getElementById("buyerEmail").innerText,
      buyer_name: document.getElementById("buyerName").innerText,
      buyer_tel: document.getElementById("buyerPhone").innerText,
    },
    (rsp) => {
      if (rsp.success) {
        console.log("결제 성공:", rsp);

        alert(
          eventType === "exhibition"
            ? "무료 전시 예약이 완료되었습니다! (0원 처리)"
            : "결제가 완료되었습니다!"
        );

        // 서버로 결제 결과 전달
        fetch("/payment/complete", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            impUid: rsp.imp_uid,
            merchantUid: rsp.merchant_uid,
            payMuch: eventType === "exhibition" ? 0 : rsp.paid_amount,
            payWhat: rsp.pay_method,
            payOk: "Y",
            boardNo: boardNo,
            memberNo: memberNo,
          }),
        })
          .then((res) => res.json())
          .then((data) => {
            if (data.result > 0) {
              window.location.href = "/payment/success";
            } else {
              alert("서버 저장 실패");
            }
          });
      } else {
        console.error("결제 실패:", rsp);
        alert("결제 실패: " + rsp.error_msg);
      }
    }
  );
}
