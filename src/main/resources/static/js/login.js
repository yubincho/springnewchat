$(document).ready(function() {
    $("#login-form").on("submit", function(event) {
        event.preventDefault();  // 기본 폼 제출 방지

        var data = {
            username: $("#username").val(),
            password: $("#password").val()
        };

        // var token;

        $.ajax({
            url: "/api/login",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({
                username: $("#username").val(),
                password: $("#password").val()
            }),
            success: function(response, status, xhr) {
                var authorizationHeader = xhr.getResponseHeader('Authorization');
                if (authorizationHeader) {
                    var accessToken = authorizationHeader.replace('Bearer ', '').trim();
                    var refreshTokenHeader = xhr.getResponseHeader('Refresh-Token');
                    var refreshToken = refreshTokenHeader ? refreshTokenHeader.replace('Bearer ', '').trim() : null;

                    // 토큰 저장
                    localStorage.setItem('jwt_token', accessToken);
                    if (refreshToken) {
                        localStorage.setItem('refreshToken', refreshToken);
                    }

                    // 로그인 성공 시 채팅 페이지로 리다이렉트
                    window.location.href = "/chat";
                } else {
                    alert("Authorization 헤더를 찾을 수 없습니다.");
                }
            },
            error: function(xhr, status, error) {
                alert("Login failed: " + xhr.responseText);
            }
        });

    });

});