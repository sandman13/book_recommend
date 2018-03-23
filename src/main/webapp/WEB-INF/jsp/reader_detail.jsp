<%--
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
    <script src="https://cdn.bootcss.com/jquery/1.12.4/jquery.min.js"></script>
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
                <li><a href="#">萨尔</a></li>
                <li><a href="#">伐木机</a></li>
                <li><a href="#">斯温</a></li>
                <li><a href="#">拍拍熊</a></li>
            </ul>
        </div>
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
            <h1 class="page-header">图书列表</h1>
            <div class="table-responsive">
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th>书名</th>
                        <th>详情</th>
                        <th>地址</th>
                        <th>状态</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="book" items="${bookList}">
                        <tr>
                            <td>${book.bookName}</td>
                            <td>${book.introduction}</td>
                            <td>${book.location}</td>
                            <c:choose>
                                <c:when test="${book.bookStatus=='CAN_BORROW'}">
                                    <td>可借阅</td>
                                </c:when>
                                <c:otherwise>
                                    <td>已借出</td>
                                </c:otherwise>
                            </c:choose>

                            <c:choose>
                                <c:when test="${book.bookStatus=='CAN_BORROW'}">
                                    <td>
                                        <button class="btn btn-success btn-sm" id="${book.bookId}">
                                            <span class="glyphicon glyphicon-eye-open"></span>
                                            借阅
                                        </button>
                                    </td>
                                </c:when>
                                <c:otherwise>
                                    <td>
                                        <button class="btn btn-danger btn-sm disabled">
                                            <span class="glyphicon glyphicon-eye-close"></span>
                                            借阅
                                        </button>
                                    </td>
                                </c:otherwise>
                            </c:choose>
                        </tr>
                        <script>
                            $(document).ready(function () {
                                /**
                                 * 提交订阅的ajax,别问我为啥写到jsp里面,因为前段分层我管不着
                                 */
                                $("#${book.bookId}").click(function () {
                                    $.ajax({
                                        type: "GET",
                                        url: "/borrow/" +${book.bookId},
                                        cache: false,
                                        async: true,
                                        success: function (data) {
                                            console.log(data);
                                            if (data.baseResult.success) {
                                                solveSuccess("借阅成功");
                                            } else {
                                                solveFail(data.baseResult.message);
                                            }
                                        },
                                        error: function (data) {
                                            solveFail("系统正忙,请稍后重试");
                                        }
                                    });
                                })
                            });
                        </script>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

</body>
</html>
