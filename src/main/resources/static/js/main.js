// "use strict";
// var stompClient = null;
// var username = null;
//
// function connect(event){
//     username = $("#name").val().trim();
//     // Switch page and connect to WebSocket
//     if(username){
//         $("#username-page").addClass("d-none")
//         $("#chat-page").removeClass("d-none")
//
//         var socket = new SockJS("/ws");
//         stompClient = Stomp.over(socket);
//
//         stompClient.connect({}, onConnected, onError);
//     }
//     event.preventDefault();
// }
//
// function onConnected(){
//     // Subscribe to the topic
//     stompClient.subscribe("/topic/public", onMessageReceived);
//
//     // Send username to the server
//     stompClient.send("/app/chat.addUser",
//         {},
//         JSON.stringify({sender: username, type: "JOIN"})
//     );
//
//     // Hide connecting message
//     $(".connecting").addClass("d-none");
// }
//
// function onError(error){
//     $(".connecting").text("Could not connect to the WebSocket server. Please refresh this page to try again!").css("color", "red");
// }
//
// function sendMessage(event){
//     var messageContent = $("#message").val().trim();
//     if(messageContent && stompClient){
//         var chatMessage = {
//             sender: username,
//             content: $("#message").val(),
//             type: "CHAT"
//         };
//         stompClient.send("/app/chat.sendMessage",{},JSON.stringify(chatMessage));
//         $("#message").val("");
//     }
//     event.preventDefault();
// }
//
// function onMessageReceived(payload){
//     var message = JSON.parse(payload.body);
//     if(message.type === "JOIN"){
//         $("#message-area").prepend(`<tr><td class="text-secondary fs-6">${message.sender} joined!</td></tr>`);
//     } else if(message.type === "LEAVE"){
//         $("#message-area").prepend(`<tr><td class="text-secondary fs-6">${message.sender} left!</td></tr>`);
//     } else {
//         $("#message-area").prepend(`<tr><td class="fs-5"><b>${message.sender} :</b> ${message.content}</td></tr>`);
//     }
// }
//
// // Add EventListener
// $("#username-form").on("submit", connect);
// $("#message-form").on("submit", sendMessage);