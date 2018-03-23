$(document).ready(function () {

    $("#loginButton").click(function () {
        var username = $("#id").val();
        var password = $("#passwd").val();

        if (username == '' && password == '') {
            $("#info").text("账号和密码不能为空");
        }
        else if (username == '') {
            $("#info").text("账号不能为空");
        }
        else if (password == '') {
            $("#info").text("密码不能为空");
        }
        else {
            $.ajax({
                type: "POST",
                url: "/login/check",
                data: {
                    username: username,
                    password: password,
                },
                dataType: "json",
                success: function (data) {
                    if (!data.success) {
                        $("#info").text(data.message);
                    } else if (data.success) {
                        $("#info").text("登陆成功，跳转中...");
                        if (data.result == 'ADMIN') {
                            window.location.href = "/admin/index";
                        }
                        else if (data.result == 'READER') {
                            window.location.href = "/reader/index";
                        } else {
                            $("#info").text("系统繁忙,请稍后重试");
                        }
                    }
                }
            });
        }
    })
});