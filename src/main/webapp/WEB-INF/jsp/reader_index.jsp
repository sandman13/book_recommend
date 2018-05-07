<%--
  Created by IntelliJ IDEA.
  User: SONG
  Date: 2018/3/20
  Time: 14:18
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
    <title>读者主页</title>
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
                <c:forEach var="recommendDTO" items="${kmeans}">
                    <li><a href="/book/${recommendDTO.bookName}/${recommendDTO.author}">${recommendDTO.bookName}</a>
                    </li>
                </c:forEach>
            </ul>
        </div>
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
            <h1 class="page-header">图书列表</h1>
            <div class="table-responsive">
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th>书名</th>
                        <th>作者</th>
                        <th>出版社</th>
                        <th>出版日期</th>
                        <th>库存数量</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="book" items="${bookList}">
                        <tr>
                            <td>${book.bookName}</td>
                            <td>${book.author}</td>
                            <td>${book.publisher}</td>
                            <td>${book.pubdate}</td>
                            <td>${book.stock}</td>
                            <td><a href="/book/${book.bookName}/${book.author}">
                                <button class="btn btn-danger btn-sm">
                                    <span class="glyphicon glyphicon-zoom-in"></span>
                                    详情
                                </button>
                            </a></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

</body>
</html>
