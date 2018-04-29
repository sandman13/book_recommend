<%@ page import="book.domain.dto.UserDTO" %><%--
  Created by IntelliJ IDEA.
  User: SONG
  Date: 2018/3/21
  Time: 6:24
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page isELIgnored="false" %>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script src="<%=request.getContextPath() %>/static/book-js/show.js"></script>
    <link href="<%=request.getContextPath() %>/static/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath() %>/static/book-css/book_index.css" rel="stylesheet">
    <script src="<%=request.getContextPath() %>/static/bootstrap/js/bootstrap.min.js"></script>
    <script src="<%=request.getContextPath() %>/static/book-js/inform.js"></script>
    <script src="<%=request.getContextPath() %>/static/notify/js/bootstrap-notify.min.js"></script>
    <link href="<%=request.getContextPath() %>/static/notify/css/animate.css"/>

    <title>书籍详情</title>
</head>
<body>
<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="/reader/index">图书管理系统</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav navbar-left">
                <li>
                    <a href="/reader/info">个人信息</a>
                </li>
                <li>
                    <a href="/reader/borrow">我的借还</a>
                </li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <li><a href="/logout"><span class="glyphicon glyphicon-log-in"></span>&nbsp退出</a></li>
            </ul>
            <form class="navbar-form navbar-right" action="/reader/search" method="post">
                <div class="form-group">
                    <input type="text" name="condition" class="form-control" placeholder="(书名,作者)">
                </div>
                <button type="submit" class="btn btn-success">搜索</button>
            </form>
        </div>
    </div>
</nav>



<div class="container-fluid">
    <div class="row">
        <div class="col-sm-3 col-md-2 sidebar">
            <ul class="nav nav-sidebar">
                <li class="active"><a href=#">使用协同过滤推荐书籍</a></li>
                <c:forEach var="recommendDTO" items="${recommendDTOList}">
                    <li><a href="/book/${recommendDTO.bookName}/${recommendDTO.author}">${recommendDTO.bookName}</a>
                    </li>
                </c:forEach>
            </ul>
            <ul class="nav nav-sidebar">
                <li class="active"><a href="#">使用knn推荐书籍</a></li>
                <li><a href="#">待扩展</a></li>
            </ul>
        </div>
        <%
            UserDTO userDTO = (UserDTO) request.getAttribute("userDTO");

        %>
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
            <h1 class="page-header">个人信息</h1>
               <div class="well well-lg">
                <form class="form-horizontal" action="/user/update">
                    <div class="form-group">
                        <label class="col-sm-2 control-label">用户名</label>
                        <div class="col-sm-10"
                            <p class="form-control-static" ><%= userDTO.getUsername()%></p>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-10">
                            <input type="hidden" class="form-control"  name="userId" value="<%= userDTO.getUserId()%>">
                        </div>
                        <div class="col-sm-10">
                            <input type="hidden" class="form-control"  name="username" value="<%= userDTO.getUsername()%>">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="inputPassword" class="col-sm-2 control-label">密码</label>
                        <div class="col-sm-10">
                            <input type="password" class="form-control" id="inputPassword" name="password" placeholder="Password" value="<%= userDTO.getPassword()%>">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">邮箱</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" name="email" placeholder="email" value="<%= userDTO.getEmail()!=null?userDTO.getEmail():""%>">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">手机</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" name="phoneNumber" placeholder="phone" value="<%= userDTO.getPhoneNumber()!=null?userDTO.getPhoneNumber():""%>">
                        </div>
                    </div>
                    <button type="submit" class="btn btn-success col-md-offset-9">修改</button>
                </form>
            </div>
        </div>
    </div>
</div>

</body>
</html>
