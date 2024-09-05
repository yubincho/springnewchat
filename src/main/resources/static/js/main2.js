'use strict';

let stompClient = null;
let username = null;
let chatRoomId = null;

// 채팅방에 연결하는 함수
function connectToRoom(event) {
    event.preventDefault();
    const roomName = $("#room-name").val().trim();
    if (roomName) {
        $.ajax({
            url: '/api/chatroom',
            type: 'POST',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('jwt_token')
            },
            data: JSON.stringify({ name: roomName }),
            contentType: 'application/json',
            success: function(response) {
                chatRoomId = response.id;
                $("#room-id").text(roomName);
                $("#room-selection-page").addClass("d-none");
                $("#chat-page").removeClass("d-none");
                connect();
            },
            error: function(xhr, status, error) {
                console.error("Error creating/joining room:", xhr.responseText || error);
                alert("Failed to create/join the room. Please try again.");
            }
        });
    }
}

// WebSocket에 연결하는 함수
function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    const headers = {
        'Authorization': 'Bearer ' + localStorage.getItem('jwt_token')
    };

    stompClient.connect(headers, onConnected, onError);
}

// WebSocket 연결이 성공했을 때 호출되는 함수
function onConnected() {
    stompClient.subscribe('/topic/chat/' + chatRoomId, onMessageReceived);

    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({ chatRoomId: chatRoomId, type: 'JOIN' })
    );

    $(".connecting").addClass('d-none');
}

// WebSocket 연결 오류 발생 시 호출되는 함수
function onError(error) {
    console.log('Could not connect to WebSocket server. Please refresh this page to try again!', error);
    $(".connecting").text('Could not connect to WebSocket server. Please refresh this page to try again!');
    $(".connecting").css("color", "red");
}

// 채팅 메시지를 전송하는 함수
function sendMessage(event) {
    const messageContent = $("#message").val().trim();
    if (messageContent && stompClient) {
        const chatMessage = {
            content: messageContent,
            chatRoomId: chatRoomId,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        $("#message").val('');
    }
    event.preventDefault();
}

// WebSocket에서 메시지를 받았을 때 호출되는 함수
function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);
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

// 사용 가능한 채팅방을 불러오는 함수
function loadChatRooms() {
    $.ajax({
        url: '/api/chatroom',
        type: 'GET',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('jwt_token')
        },
        success: function(rooms) {
            const roomList = $('#available-rooms');
            roomList.empty();
            rooms.forEach(room => {
                roomList.append(`<div class="room-item" data-room-id="${room.id}">${room.name}</div>`);
            });
        },
        error: function(xhr, status, error) {
            console.error("Error loading chat rooms:", xhr.responseText || error);
        }
    });
}

// 페이지 로드 시 실행되는 초기화 함수
$(document).ready(function() {
    const token = localStorage.getItem('jwt_token');
    if (!token) {
        window.location.href = '/login';
    } else {
        $("#room-selection-page").removeClass("d-none");
        loadChatRooms();
    }

    $("#room-form").on('submit', connectToRoom);
    $("#message-form").on('submit', sendMessage);

    $(document).on('click', '.room-item', function() {
        const roomId = $(this).data('room-id');
        const roomName = $(this).text();
        chatRoomId = roomId;
        $("#room-id").text(roomName);
        $("#room-selection-page").addClass("d-none");
        $("#chat-page").removeClass("d-none");
        connect();
    });
});
