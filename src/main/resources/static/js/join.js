var Join = {
    addEvent: function () {
        $('button').bind('click', function (event) {
            Join.eventControl(event);
        });
    },

    eventControl: function (event) {
        var target = event.currentTarget;
        switch (target.id) {
            case "btnJoin":
                Join.join();
                break;
        }
    },

    join: function() {

        if( $("#username").val() =='' || $("#password").val() == '' || $("#userId").val() == ''){
            alert('필수정보를 입력해주세요.\n' );
            return false;
        }

        var data = {
            username: $("#username").val(),
            password: $("#password").val(),
            userId: $("#userId").val()
        };

        $.ajax({
            url: "/join",
            type: "POST",
            data: JSON.stringify(data),
            contentType: "application/json; charset=UTF-8",
            success: function (data) {
                if(data.hasError){
                    alert(data.errorInfo.message);
                    return;
                }
                alert("회원가입이 완료되었습니다.");
                location.href = "/login";
            },
            error: function(request,status,error){
                alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
            }
        });
    }
}

$(function () {
    Join.addEvent();
})