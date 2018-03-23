<%--
  Created by IntelliJ IDEA.
  User: SONG
  Date: 2018/3/19
  Time: 16:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>用户登录</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script src="https://cdn.bootcss.com/jquery/1.12.4/jquery.min.js"></script>
    <link href="<%=request.getContextPath() %>/static/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <script src="<%=request.getContextPath() %>/static/bootstrap/js/bootstrap.min.js"></script>
    <script src="<%=request.getContextPath() %>/static/book-js/iframe.js"></script>
    <script src="<%=request.getContextPath() %>/static/book-js/login.js"></script>
    <style>
        #login {
           padding-top:200px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="col-md-4 col-md-offset-4" id="login">
        <div class="panel panel-default">
            <div class="panel-heading" style="background-color: #fff">
                <h3 class="panel-title">请登录</h3>
            </div>
            <div class="panel-body">
                <div class="form-group">
                    <label for="id">用户名</label>
                    <input type="text" class="form-control" id="id" placeholder="请输入用户名">
                </div>
                <div class="form-group">
                    <label for="passwd">密码</label>
                    <input type="password" class="form-control" id="passwd" placeholder="请输入密码">
                </div>
                <p style="text-align: right;color: red;position: absolute" id="info"></p><br/>
                <button id="loginButton" class="btn btn-primary  btn-block">登陆
                </button>
            </div>
        </div>
    </div>
</div>
</body>
</html>
