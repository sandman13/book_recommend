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
    <title>用户注册</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script src="<%=request.getContextPath() %>/static/book-js/show.js"></script>
    <link href="<%=request.getContextPath() %>/static/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <script src="<%=request.getContextPath() %>/static/bootstrap/js/bootstrap.min.js"></script>
    <style>
        #login {
            padding-top: 200px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="col-md-4 col-md-offset-4" id="login">
        <div class="panel panel-default">
            <div class="panel-heading" style="background-color: #fff">
                <h3 class="panel-title">请注册</h3>
            </div>
            <div class="panel-body">
                <form action="/register">
                    <div class="form-group">
                        <label for="id">用户名</label>
                        <input type="text" class="form-control" id="id" name="username" placeholder="请输入用户名">
                    </div>
                    <div class="form-group">
                        <label for="passwd">密码</label>
                        <input type="password" class="form-control" id="passwd" name="password" placeholder="请输入密码">
                    </div>
                    <div class="form-group">
                        <label for="passwd">年龄</label>
                        <input type="password" class="form-control" name="age" placeholder="请输入年龄">
                    </div>
                    <div class="form-group">
                        <label for="passwd">性别</label>
                        <div class="radio">
                            <label>
                         <input type="radio" id="male" name="sex" value="1"/> 男性
                            </label>
                        </div>
                        <div class="radio">
                            <label>
                          <input type="radio" id="female" name="sex" value="0"/>女性
                            </label>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="passwd">职业</label>
                    <select class="form-control" name="profession">
                        <option value ="教师">教师</option>
                        <option value ="IT">IT</option>
                        <option value="医生">医生</option>
                    </select>
                    </div>
                    <p style="text-align: right;color: red;position: absolute" id="info"></p><br/>
                    <button id="loginButton" type="submit" class="btn btn-primary  btn-block">注册
                    </button>
                    <button id="registerButton" class="btn btn-warning  btn-block"><a href=" href=" toLogin">已有账户去登陆</a>
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>
