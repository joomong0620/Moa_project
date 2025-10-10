console.log("pay.js loaded")
console.log("결제창 테스트 테스트!!! 서버 연동 안함")
// 1. 금액 계산
const seatSelect = document.getElementById("seatSelect");
const totalPrice = document.getElementById("totalPrice");
seatSelect.addEventListener("change", () => {
  const price = seatSelect.options[seatSelect.selectedIndex].dataset.price;
  totalPrice.textContent = price ? parseInt(price).toLocaleString() : 0;
});

// 2. 결제 요청
document.getElementById("payNowBtn").addEventListener("click", () => {
  const selected = seatSelect.options[seatSelect.selectedIndex];
  const price = selected.dataset.price ? parseInt(selected.dataset.price) : 0;
  if (!price) return alert("좌석을 선택하세요.");

  requestPay(price);
});

// 3. KG이니시스 결제창 호출
function requestPay(amount) {
  const IMP = window.IMP;
  IMP.init("imp80522717"); // ← 가맹점 식별코드

  IMP.request_pay(
    {
      // pg를 아래와 같이 작성할 경우 실제 결제가 되니 조심! -> 관리자 콘솔 가서 취소해주어야 함
      // html5_inicisTest 로 작성해야 실제 결제가 이루어지지 않음.
      // pg사 코드를 작성할 때는 공식 문서에서 pg코드를 참조해서 적을 것
      pg: "html5_inicis", // PG사 (테스트용)
      pay_method: "card", // 카드결제
      merchant_uid: "order_" + new Date().getTime(), // 고유한 주문번호
      name: "뮤지컬 렌트 예매", // 상품명
      amount: amount, // 결제금액
      buyer_email: "user@example.com", // 사용자 이메일
      buyer_name: "홍길동", // 주문자 이름
      buyer_tel: "010-1234-5678", // 주문자 전화번호
      buyer_addr: "서울특별시 강남구", // 주문자의 주소지(필요하지 않으면 제거해도 됨)
      buyer_postcode: "123-456", // 주문자 도로명 주소(마찬가지)
    },
    (rsp) => {
      if (rsp.success) {
        alert("결제가 완료되었습니다");
        console.log("결제 성공", rsp);
      } else {
        alert(`결제 실패 : ${rsp.error_msg}`);
        console.error("결제 실패", rsp);
      }
    }
  );
}
