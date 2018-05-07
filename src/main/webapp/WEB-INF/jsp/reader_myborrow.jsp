<%--
  Created by IntelliJ IDEA.
  User: SONG
  Date: 2018/3/21
  Time: 13:59
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
                <li class="active">
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
                <c:forEach var="recommendDTO" items="${recommendList}">
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
                        <th>个人评分</th>
                        <th>借阅时间</th>
                        <th>归还时间</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="borrow" items="${borrowDTOList}">
                        <tr>
                            <td>${borrow.bookDTO.bookName}</td>
                            <c:choose>
                                <c:when test="${borrow.goal==0}">
                                    <td>尚未评分</td>
                                </c:when>
                                <c:otherwise>
                                    <td>${borrow.goal}</td>
                                </c:otherwise>
                            </c:choose>
                            <td>${borrow.borrowDate}</td>
                            <td>${borrow.backDate}</td>
                            <!--如果尚未评分则可以显示评分字样-->
                            <div class="modal fade" id="${borrow.borrowId}" tabindex="-1" role="dialog"
                                 aria-labelledby="myModalLabel" aria-hidden="true">
                                <div class="modal-dialog">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                                                &times;
                                            </button>
                                            <h4 class="modal-title" id="myModalLabel">
                                                评价
                                            </h4>
                                        </div>
                                        <div class="modal-body">
                                            <form class="form-horizontal">
                                                <div class="form-group">
                                                    <label>评价</label>
                                                    <select class="form-control" name="${borrow.borrowId}">
                                                        <option value="5">很棒(5分)</option>
                                                        <option value="4">挺不错的(4分)</option>
                                                        <option value="3">一般(3分)</option>
                                                        <option value="2">不是很满意(2分)</option>
                                                        <option value="1">很差(1分)</option>
                                                    </select>
                                                </div>
                                            </form>
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-primary" name="${borrow.borrowId}">
                                                评分
                                            </button>
                                        </div>
                                    </div><!-- /.modal-content -->
                                </div><!-- /.modal -->
                            </div>

                            <c:choose>
                                <c:when test="${borrow.goal==0}">
                                    <td>
                                        <button class="btn btn-success btn-sm" data-toggle="modal"
                                                data-target="#${borrow.borrowId}">
                                            <span class="glyphicon glyphicon-pencil"></span>
                                            评分
                                        </button>
                                    </td>
                                </c:when>
                                <c:otherwise>
                                    <td>
                                        <button class="btn btn-danger btn-sm" data-toggle="modal"
                                                data-target="#${borrow.borrowId}">
                                            <span class="glyphicon glyphicon-pencil"></span>
                                            修改
                                        </button>
                                    </td>
                                </c:otherwise>
                            </c:choose>
                        </tr>
                        <script>
                            $(document).ready(function () {
                                $("button[name='${borrow.borrowId}']").click(function () {
                                    $.ajax({
                                        type: "POST",
                                        url: "/borrow/evaluate/" +${borrow.borrowId},
                                        data: {
                                            goal: $("select[name='${borrow.borrowId}']").val()
                                        },
                                        dataType: "json",
                                        success: function (data) {
                                            if (data.baseResult.success) {
                                                window.location.href = "/reader/borrow";
                                            } else {
                                                solveFail(data.baseResult.message);
                                            }
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
