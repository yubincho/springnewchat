'use strict';

// JWT 토큰 가져오기
function getJwtToken() {
    return localStorage.getItem('jwt_token');
}

// 채팅방에 연결하는 함수
function connectToRoom(event) {
    event.preventDefault();
    const roomName = $("#room-name").val().trim();  // 사용자로부터 방 이름 입력받기
    const token = getJwtToken();

    if (!token) {
        alert("로그인이 필요합니다.");
        window.location.href = '/login';
        return;
    }

    if (roomName) {
        $.ajax({
            url: '/api/chatroom',  // 실제 방 생성 API 엔드포인트
            type: 'POST',
            headers: {
                'Authorization': token
            },
            data: JSON.stringify({ name: roomName }),  // 방 이름을 서버로 전송
            contentType: 'application/json',
            success: function(response) {
                const chatRoomId = response.id;  // 서버로부터 생성된 방 ID를 받음
                window.location.href = `/chat/${chatRoomId}?roomName=${encodeURIComponent(roomName)}`;  // 방으로 이동
            },
            error: function(xhr, status, error) {
                console.error("Error creating/joining room:", xhr.responseText || error);
                alert("방 생성/참가에 실패했습니다. 다시 시도해주세요.");
            }
        });
    }
}

// 초기화 함수
$(document).ready(function() {
    $("#room-form").on('submit', connectToRoom);
});
