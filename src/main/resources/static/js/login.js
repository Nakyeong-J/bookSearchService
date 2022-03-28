var Login = {
    addEvent: function () {
        $('button').bind('click', function (event) {
            Login.eventControl(event);
        });
    },

    eventControl: function (event) {
        var target = event.currentTarget;
        switch (target.id) {
            case "btnLogin":
                Login.login();
                break;
        }
    },

    login: function(){
        var data = {
            password: $("#password").val(),
            userId: $("#userId").val()
        };
        $.ajax({
            url: "/login",
            type: "POST",
            data: JSON.stringify(data),
            contentType: "application/json; charset=UTF-8",
            success : function(data){
                if(data.hasError){
                    alert(data.errorInfo.message);
                    return;
                }

                sessionStorage.Token = data.resultData;
                console.log("sessionStorage.Token : ", sessionStorage.Token );
                location.href = "/board";

            },error: function(request,status,error){
                alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
            }
        });
    },
}

$(function () {
    Login.addEvent();
});