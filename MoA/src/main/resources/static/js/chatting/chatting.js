console.log("chatting.js loaded");

// ========== 전역 변수 ==========
let selectChattingNo = null;
let selectTargetNo = null;
let selectMemberCount = null;
let selectTargetName = null;
let selectTargetProfile = null;
let chattingSock = null;

// ========== SockJS WebSocket 연결 (페이지 로드 시 한 번만!) ==========
if (loginMemberNo && loginMemberNo !== 0) {
  chattingSock = new SockJS("/chattingSock");
  
  chattingSock.onmessage = (e) => {
    const msg = JSON.parse(e.data);
    console.log("메시지 수신:", msg);
    
    // 현재 보고 있는 채팅방의 메시지인 경우
    if (selectChattingNo == msg.chattingNo) {
      // 내가 보낸 메시지가 아닐 때만 추가 (내 메시지는 이미 추가됨)
      if (msg.senderNo != loginMemberNo) {
        addMessageToUI(msg);
      }
      markAsRead();
    } else {
      // 다른 채팅방의 메시지 → 알람 배지 표시
      showNotificationBadge(msg.chattingNo);
    }
    
    // 마지막 메시지 업데이트
    updateLastMessage(msg.chattingNo, msg.messageContent, msg.sendTime);
  };

  chattingSock.onopen = () => {
    console.log("WebSocket 연결 성공");
  };

  chattingSock.onclose = () => {
    console.log("WebSocket 연결 종료");
  };

  chattingSock.onerror = (error) => {
    console.error("WebSocket 에러:", error);
  };
}

// ========== 페이지 로드 시 초기화 ==========
document.addEventListener("DOMContentLoaded", () => {
  initRoomClickEvents();
  initModalEvents();
  initMessageEvents();
});

// ========== 모달 이벤트 초기화 ==========
function initModalEvents() {
  const teamModal = document.getElementById("teamModal");
  const personalModal = document.getElementById("personalModal");

  document.querySelector(".teamPlus").addEventListener("click", () => {
    teamModal.style.display = "flex";
  });

  document.querySelector(".personalPlus").addEventListener("click", () => {
    personalModal.style.display = "flex";
  });

  document.querySelectorAll(".close").forEach(btn => {
    btn.addEventListener("click", () => {
      btn.closest(".modal").style.display = "none";
    });
  });

  window.addEventListener("click", (e) => {
    if (e.target.classList.contains("modal")) {
      e.target.style.display = "none";
    }
  });

  window.addEventListener("keydown", (e) => {
    if (e.key === "Escape") {
      teamModal.style.display = "none";
      personalModal.style.display = "none";
    }
  });

  document.getElementById("teamForm").addEventListener("submit", createTeamRoom);
  document.getElementById("personalStart").addEventListener("click", startPersonalChat);
}

// ========== 채팅방을 목록 맨 위로 이동 ==========
function moveRoomToTop(chattingNo) {
  const roomElement = document.querySelector(`[data-chat-no="${chattingNo}"]`);
  if (!roomElement) return;
  const parentList = roomElement.parentElement;
  if (parentList && parentList.firstChild !== roomElement) {
    parentList.insertBefore(roomElement, parentList.firstChild);
  }
}

// ========== 팀 채팅방 생성 ==========
async function createTeamRoom(e) {
  e.preventDefault();
  
  const selectedMembers = Array.from(
    document.querySelectorAll("#teamMemberList input[type=checkbox]:checked")
  ).map(el => el.value);

  if (selectedMembers.length === 0) {
    alert("최소 2명 이상의 참여자를 선택하세요.");
    return;
  }

  try {
    const response = await fetch("/chatting/enter", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(selectedMembers)
    });

    const chattingNo = await response.text();
    if (parseInt(chattingNo) > 0) {
      alert("팀 채팅방이 생성되었습니다.");
      document.getElementById("teamModal").style.display = "none";
      document.getElementById("teamForm").reset();
      await updateRoomList();
      selectChattingNo = chattingNo;
      const newRoom = document.querySelector(`[data-chat-no="${chattingNo}"]`);
      if (newRoom) selectRoom(newRoom);
    } else {
      alert("채팅방 생성에 실패했습니다.");
    }
  } catch (error) {
    console.error("팀 채팅방 생성 오류:", error);
  }
}

// ========== 알람 배지 표시 ==========
function showNotificationBadge(chattingNo) {
  const roomElement = document.querySelector(`[data-chat-no="${chattingNo}"]`);
  if (!roomElement) return;
  
  let badge = roomElement.querySelector(".badge");
  if (badge) {
    const currentCount = parseInt(badge.textContent) || 0;
    badge.textContent = currentCount + 1;
  } else {
    badge = document.createElement("span");
    badge.className = "badge";
    badge.textContent = "1";
    roomElement.appendChild(badge);
  }
}

// ========== 팀 채팅방 목록 업데이트 ==========
function updateTeamRoomList(teamRooms) {
  const teamRoomList = document.querySelector(".chat-room-list");
  teamRoomList.innerHTML = "";

  teamRooms.forEach(room => {
    const li = document.createElement("li");
    li.classList.add("chat-room");
    li.setAttribute("data-chat-no", room.chattingNo);
    li.setAttribute("data-member-count", room.memberCount);
    li.setAttribute("data-room-name", room.roomName);
    if (room.chattingNo == selectChattingNo) li.classList.add("active");

    const lastMsg = room.lastMessage 
      ? (room.lastMessage.length > 20 ? room.lastMessage.substring(0, 20) + '...' : room.lastMessage)
      : '메시지가 없습니다';

    li.innerHTML = `
      <img src="${room.roomImage || '/images/common/user.svg'}" alt="team" />
      <div>
        <p class="room-name">${room.targetNickName}</p>
        <p class="room-desc">${lastMsg}</p>
      </div>
      ${room.notReadCount > 0 && room.chattingNo != selectChattingNo 
        ? `<span class="badge">${room.notReadCount}</span>` : ''}
    `;
    teamRoomList.appendChild(li);
  });
}

// ========== 개인 채팅방 목록 업데이트 ==========
function updatePersonalRoomList(personalRooms) {
  const personalRoomList = document.querySelector(".chat-person-list");
  personalRoomList.innerHTML = "";

  personalRooms.forEach(room => {
    const li = document.createElement("li");
    li.classList.add("person-chat");
    li.setAttribute("data-chat-no", room.chattingNo);
    li.setAttribute("data-target-no", room.targetNo);
    li.setAttribute("data-member-count", room.memberCount);
    if (room.chattingNo == selectChattingNo) li.classList.add("active");

    const lastMsg = room.lastMessage 
      ? (room.lastMessage.length > 20 ? room.lastMessage.substring(0, 20) + '...' : room.lastMessage)
      : '';

    li.innerHTML = `
      <img src="${room.targetProfile || '/images/common/user.svg'}" alt="profile" />
      <div>
        <p class="person-name">${room.targetNickName}</p>
        <p class="last-msg">${lastMsg}</p>
      </div>
      ${room.notReadCount > 0 && room.chattingNo != selectChattingNo 
        ? `<span class="badge">${room.notReadCount}</span>` : ''}
    `;
    personalRoomList.appendChild(li);
  });
}

// ========== 개인 채팅 시작 ==========
async function startPersonalChat() {
  const selected = document.querySelector("#personalMemberList input[name=personal]:checked");
  if (!selected) {
    alert("대화 상대를 선택하세요.");
    return;
  }
  const targetNo = parseInt(selected.value);

  try {
    const response = await fetch("/chatting/enter", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify([loginMemberNo, targetNo])
    });

    const chattingNo = await response.text();
    if (parseInt(chattingNo) > 0) {
      alert("개인 채팅방이 생성되었습니다.");
      document.getElementById("personalModal").style.display = "none";
      document.getElementById("personalForm").reset();
      await updateRoomList();
      selectChattingNo = chattingNo;
      const newRoom = document.querySelector(`[data-chat-no="${chattingNo}"]`);
      if (newRoom) selectRoom(newRoom);
    } else {
      alert("채팅방 생성에 실패했습니다.");
    }
  } catch (error) {
    console.error("개인 채팅 시작 오류:", error);
  }
}

// ========== 채팅방 클릭 이벤트 초기화 ==========
function initRoomClickEvents() {
  document.querySelectorAll(".chat-room").forEach(room => {
    room.addEventListener("click", () => selectRoom(room));
  });
  document.querySelectorAll(".person-chat").forEach(room => {
    room.addEventListener("click", () => selectRoom(room));
  });
}

// ========== 채팅방 선택 ==========
function selectRoom(roomElement) {
  // 모든 채팅방 선택 해제
  document.querySelectorAll(".chat-room, .person-chat").forEach(el => {
    el.classList.remove("active");
  });
  roomElement.classList.add("active");

  selectChattingNo = roomElement.getAttribute("data-chat-no");
  selectMemberCount = parseInt(roomElement.getAttribute("data-member-count"));
  selectTargetNo = roomElement.getAttribute("data-target-no");

  if (selectMemberCount >= 3) {
    selectTargetName = roomElement.querySelector(".room-name").textContent;
    selectTargetProfile = roomElement.querySelector("img").getAttribute("src");
  } else {
    selectTargetName = roomElement.querySelector(".person-name").textContent;
    selectTargetProfile = roomElement.querySelector("img").getAttribute("src");
  }

  const badge = roomElement.querySelector(".badge");
  if (badge) badge.remove();

  showChatRoom();
  loadMessages();
  markAsRead();
}

// ========== 채팅방 UI 표시 ==========
function showChatRoom() {
  document.getElementById("emptyState").style.display = "none";
  document.querySelector(".chat-room-header").style.display = "flex";
  document.querySelector(".chat-messages").style.display = "block";
  document.querySelector(".chat-input-area").style.display = "flex";
  document.getElementById("currentRoomName").textContent = selectTargetName;
  document.getElementById("currentRoomImage").setAttribute("src", selectTargetProfile);
}

// ========== 메시지 목록 로드 ==========
async function loadMessages() {
  try {
    const response = await fetch(`/chatting/selectMessageList?chattingNo=${selectChattingNo}&memberNo=${loginMemberNo}`);
    const messageList = await response.json();
    const chatMessages = document.querySelector(".chat-messages");
    chatMessages.innerHTML = "";
    messageList.forEach(msg => addMessageToUI(msg));
    chatMessages.scrollTop = chatMessages.scrollHeight;
  } catch (error) {
    console.error("메시지 로드 오류:", error);
  }
}

// ========== 메시지 UI에 추가 ==========
function addMessageToUI(msg) {
  const chatMessages = document.querySelector(".chat-messages");
  const li = document.createElement("li");
  const isMyMessage = msg.senderNo == loginMemberNo;
  li.className = isMyMessage ? "message me" : "message other";

  if (isMyMessage) {
    li.innerHTML = `
      <div class="msg-content">
        <p class="msg-text">${msg.messageContent}</p>
        <span class="msg-time">${msg.sendTime}</span>
      </div>`;
  } else {
    const senderProfile = msg.senderProfile || selectTargetProfile || '/images/user.png';
    li.innerHTML = `
      <img src="${senderProfile}" alt="profile" />
      <div class="msg-content">
        <p class="msg-text">${msg.messageContent}</p>
        <span class="msg-time">${msg.sendTime}</span>
      </div>`;
  }
  chatMessages.appendChild(li);
  chatMessages.scrollTop = chatMessages.scrollHeight;
}

// ========== 메시지 전송 이벤트 초기화 ==========
function initMessageEvents() {
  const sendBtn = document.getElementById("sendBtn");
  const messageInput = document.getElementById("messageInput");
  sendBtn.addEventListener("click", sendMessage);
  messageInput.addEventListener("keypress", (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  });
}

// ========== 메시지 전송 ==========
function sendMessage() {
  const input = document.getElementById("messageInput");
  const message = input.value.trim();
  if (!message || !selectChattingNo) return;

  const messageObj = {
    senderNo: loginMemberNo,
    memberNo: loginMemberNo,
    targetNo: selectTargetNo,
    chattingNo: selectChattingNo,
    messageContent: message
  };

  chattingSock.send(JSON.stringify(messageObj));
  input.value = "";
  input.focus();

  const now = new Date();
  const sendTime = `${now.getFullYear()}.${String(now.getMonth() + 1).padStart(2, '0')}.${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`;
  addMessageToUI({ senderNo: loginMemberNo, messageContent: message, sendTime });
  updateLastMessage(selectChattingNo, message, sendTime);
}

// ========== 마지막 메시지 업데이트 ==========
function updateLastMessage(chattingNo, message, sendTime) {
  const roomElement = document.querySelector(`[data-chat-no="${chattingNo}"]`);
  if (!roomElement) return;
  
  const displayMsg = message.length > 20 ? message.substring(0, 20) + '...' : message;
  
  // 팀 채팅방인 경우
  const roomDesc = roomElement.querySelector(".room-desc");
  if (roomDesc) {
    roomDesc.textContent = displayMsg;
  }
  
  // 개인 채팅방인 경우
  const lastMsg = roomElement.querySelector(".last-msg");
  if (lastMsg) {
    lastMsg.textContent = displayMsg;
  }
  
  moveRoomToTop(chattingNo);
}

// ========== 읽음 처리 ==========
async function markAsRead() {
  if (!selectChattingNo || !loginMemberNo) return;
  try {
    await fetch("/chatting/updateReadFlag", {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ memberNo: loginMemberNo, chattingNo: selectChattingNo })
    });
    console.log("읽음 처리 완료");
  } catch (error) {
    console.error("읽음 처리 오류:", error);
  }
}

// ========== 채팅방 목록 갱신 ==========
async function updateRoomList() {
  try {
    const response = await fetch("/chatting/roomList");
    const roomList = await response.json();
    updateTeamRoomList(roomList.filter(r => r.memberCount >= 3));
    updatePersonalRoomList(roomList.filter(r => r.memberCount === 2));
    initRoomClickEvents();
  } catch (error) {
    console.error("채팅방 목록 갱신 오류:", error);
  }
}

// ========== 채팅방 나가기 ==========
document.addEventListener("DOMContentLoaded", () => {
  const exitBtn = document.querySelector(".exit-btn");
  
  if (exitBtn) {
    exitBtn.addEventListener("click", async () => {
      if (!confirm("채팅방을 나가시겠습니까?")) return;

      try {
        const response = await fetch("/chatting/exitRoom", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            memberNo: loginMemberNo,
            chattingNo: selectChattingNo
          })
        });

        const result = await response.json();
        if (result.success) {
          alert("채팅방을 나갔습니다.");
          location.reload();
        } else {
          alert("채팅방 나가기에 실패했습니다.");
        }
      } catch (error) {
        console.error("채팅방 나가기 오류:", error);
      }
    });
  }
});

// ========== 팀 채팅 검색 기능 ==========
document.addEventListener("DOMContentLoaded", () => {
  const teamSearch = document.getElementById("targetSearchInput");
  const teamMemberList = document.getElementById("teamMemberList");

  if (teamSearch) {
    teamSearch.addEventListener("input", async () => {
      const query = teamSearch.value.trim();

      try {
        const response = await fetch(`/chatting/selectTarget?memberNo=${loginMemberNo}&query=${query}`);
        const members = await response.json();
        teamMemberList.innerHTML = "";

        if (members.length === 0) {
          teamMemberList.innerHTML = "<p>검색 결과가 없습니다.</p>";
          return;
        }

        members.forEach(member => {
          const li = document.createElement("li");
          li.classList.add("friend-item");
          li.innerHTML = `
            <label>
              <input type="checkbox" name="teamMember" value="${member.memberNo}">
              <img src="${member.profileImg || '/images/common/user.svg'}" alt="프로필">
              <span>${member.memberNickname}</span>
            </label>
          `;
          teamMemberList.appendChild(li);
        });
      } catch (error) {
        console.error("팀 채팅 검색 오류:", error);
      }
    });
  }
});

// ========== 개인 채팅 검색 기능 ==========
document.addEventListener("DOMContentLoaded", () => {
  const personalSearch = document.getElementById("personalSearchInput");
  const personalMemberList = document.getElementById("personalMemberList");

  if (personalSearch) {
    personalSearch.addEventListener("input", async () => {
      const query = personalSearch.value.trim();

      try {
        const response = await fetch(`/chatting/selectTarget?memberNo=${loginMemberNo}&query=${query}`);
        const members = await response.json();
        personalMemberList.innerHTML = "";

        if (members.length === 0) {
          personalMemberList.innerHTML = "<p>검색 결과가 없습니다.</p>";
          return;
        }

        members.forEach(member => {
          const li = document.createElement("li");
          li.classList.add("friend-item");
          li.innerHTML = `
            <label>
              <input type="radio" name="personal" value="${member.memberNo}">
              <img src="${member.profileImg || '/images/common/user.svg'}" alt="프로필">
              <span>${member.memberNickname}</span>
            </label>
          `;
          personalMemberList.appendChild(li);
        });
      } catch (error) {
        console.error("개인 채팅 검색 오류:", error);
      }
    });
  }
});