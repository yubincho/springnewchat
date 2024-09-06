'use strict';

let stompClient = null;
let chatRoomId = null;


// JWT 토큰 가져오기
function getJwtToken() {
    return localStorage.getItem('jwt_token');
}


// WebSocket에 연결하는 함수
function connect() {
    const token = getJwtToken();
    if (!token) {
        alert("로그인이 필요합니다.");
        window.location.href = '/login';
        return;
    }

    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    // 여기서 token을 헤더에 추가합니다
    socket.onopen = function() {
        socket.send(JSON.stringify({'Authorization': token}));
    };

    stompClient.connect({}, onConnected, onError);
}

// WebSocket 연결이 성공했을 때 호출되는 함수
function onConnected() {
    stompClient.subscribe('/topic/chat/' + chatRoomId, onMessageReceived);
    const token = getJwtToken();

    const headers = {
        'Authorization': token
    };
    stompClient.send("/app/chat.addUser",
        headers,
        JSON.stringify({ chatRoomId: chatRoomId, type: 'JOIN' })
    );

    $(".connecting").addClass('d-none');
}

// WebSocket 연결 오류 발생 시 호출되는 함수
function onError(error) {
    console.log('WebSocket 서버에 연결할 수 없습니다. 페이지를 새로고침해 다시 시도해주세요!', error);
    $(".connecting").text('WebSocket 서버에 연결할 수 없습니다. 페이지를 새로고침해 다시 시도해주세요!');
    $(".connecting").css("color", "red");
}

// 채팅 메시지를 전송하는 함수
function sendMessage(event) {
    const token = getJwtToken();
    const messageContent = $("#message").val().trim();
    if (messageContent && stompClient) {

        if (!token) {
            alert("세션이 만료되었습니다. 다시 로그인해주세요.");
            window.location.href = '/login';
            return;
        }

        const chatMessage = {
            content: messageContent,
            chatRoomId: chatRoomId,
            type: 'CHAT'
        };
        const headers = {
            'Authorization': token
        };
        stompClient.send("/app/chat.sendMessage", headers, JSON.stringify(chatMessage));
        $("#message").val('');
    }
    event.preventDefault();
}

// WebSocket에서 메시지를 받았을 때 호출되는 함수
function onMessageReceived(payload) {
    console.log("onMessageReceived called with payload:", payload);
    const message = JSON.parse(payload.body);
    console.log("Received message in room: " + message.chatRoomId);

    let messageElement = '<tr><td>';
    if (message.type === 'JOIN') {
        messageElement += '<i>' + message.sender + ' joined the chat</i>';
    } else if (message.type === 'LEAVE') {
        messageElement += '<i>' + message.sender + ' left the chat</i>';
    } else {
        messageElement += '<strong>' + message.sender + ':</strong> ' + message.content;
    }
    messageElement += '</td></tr>';
    $("#message-area").append(messageElement);
    $("#message-area").scrollTop($("#message-area")[0].scrollHeight);
}

// 페이지 로드 시 실행되는 초기화 함수
function leaveChatRoom() {
    const token = getJwtToken();
    if (stompClient) {
        stompClient.send("/app/chat.leaveUser",
            {'Authorization': token},
            JSON.stringify({chatRoomId: chatRoomId, type: 'LEAVE'})
        );
        stompClient.disconnect();
    }
    window.location.href = '/chat-room.html';  // 채팅방 선택 페이지로 이동
}

$(document).ready(function() {
    const urlParams = new URLSearchParams(window.location.search);
    chatRoomId = window.location.pathname.split('/').pop();
    const roomName = urlParams.get('roomName');

    if (!chatRoomId || !roomName) {
        alert("유효하지 않은 채팅방입니다.");
        window.location.href = '/chat-room.html';
        return;
    }

    $("#room-id").text(roomName);
    connect();
    $("#message-form").on('submit', sendMessage);
    $("#leave-btn").on('click', leaveChatRoom);
});