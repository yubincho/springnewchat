'use strict';

let stompClient = null;
let chatRoomId = null;
let currentUser = null;


function setCurrentUser(user) {
    currentUser = user;
    console.log("Current user set:", currentUser);
}

function fetchCurrentUser(callback) {
    const userInfo = localStorage.getItem('user_info');
    if (userInfo) {
        try {
            const user = JSON.parse(userInfo);
            setCurrentUser(user);
            if (callback) callback();
        } catch (error) {
            console.error("Error parsing user info:", error);
        }
    } else {
        console.error("No user info found");
        // 선택적: 로그인 페이지로 리다이렉트
        window.location.href = '/login';
    }
}

function getJwtToken() {
    return localStorage.getItem('jwt_token');
}

function connect() {
    const token = getJwtToken();
    if (!token) {
        alert("로그인이 필요합니다.");
        window.location.href = '/login';
        return;
    }

    fetchCurrentUser(function() {
        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.connect({'Authorization': getJwtToken()}, onConnected, onError);
    });
}

function onConnected() {
    console.log("Connected to WebSocket");
    stompClient.subscribe('/topic/chat/' + chatRoomId, onMessageReceived);

    if (currentUser) {
        console.log("Sending join message for user:", currentUser.username);
        stompClient.send("/app/chat.addUser",
            {'Authorization': getJwtToken()},
            JSON.stringify({
                chatRoomId: chatRoomId,
                type: 'JOIN',
                sender: currentUser.username,
                userId: currentUser.id
            })
        );
    } else {
        console.error("Current user is not set");
    }

    $(".connecting").addClass('d-none');
}

function onError(error) {
    console.log('WebSocket 연결 오류:', error);
    $(".connecting").text('WebSocket 서버에 연결할 수 없습니다. 페이지를 새로고침해 다시 시도해주세요!');
    $(".connecting").css("color", "red");
}

function sendMessage(event) {
    event.preventDefault();
    const messageContent = $("#message").val().trim();
    if (messageContent && stompClient && currentUser) {
        console.log("Sending message:", messageContent);
        const chatMessage = {
            content: messageContent,
            chatRoomId: chatRoomId,
            type: 'CHAT',
            sender: currentUser.username,
            userId: currentUser.id
        };
        stompClient.send("/app/chat.sendMessage",
            {'Authorization': getJwtToken()},
            JSON.stringify(chatMessage)
        );
        $("#message").val('');
    }
}

function onMessageReceived(payload) {
    console.log("Received message payload:", payload);
    const message = JSON.parse(payload.body);
    console.log("Parsed message:", message);

    let messageElement = '<div class="message">';
    if (message.userId === currentUser.id) {
        messageElement += '<div class="message-sender">You</div>';
    } else {
        messageElement += '<div class="message-sender">' + message.sender + '</div>';
    }
    messageElement += '<div class="message-content">' + message.content + '</div>';
    messageElement += '</div>';

    $("#message-area").append(messageElement);
    $("#message-area").scrollTop($("#message-area")[0].scrollHeight);
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
});