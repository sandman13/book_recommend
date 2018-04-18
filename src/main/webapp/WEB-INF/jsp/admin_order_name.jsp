<%@ page import="java.util.Date" %>
<%@ page import="java.util.Calendar" %><%--
  Created by IntelliJ IDEA.
  User: lanzhang
  Date: 2018/1/19
  Time: 下午2:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="book.domain.dto.BookDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="com.github.pagehelper.PageInfo" %>
<%@ page import="book.domain.dto.BorrowDTO" %>


<html>
<head>
    <meta charset="utf-8">
    <title>图书管理系统</title>
    <link rel="stylesheet" href="<%= request.getContextPath()%>/static/bootstrap/css/bootstrap.min.css">
    <script src="<%= request.getContextPath() %>/static/book-js/show.js"></script>
    <script src="<%= request.getContextPath()%>/static/bootstrap/js/bootstrap.min.js"></script>
    <script>

        function ShowTip(tip, type) {
            var $tip = $('#tip');
            if ($tip.length == 0) {
                $tip = $('<span id="tip" style="font-weight:bold;position:absolute;top:50px;left: 50%;z-index:9999"></span>');
                $('body').append($tip);
            }
            $tip.stop(true).attr('class', 'alert alert-' + type).text(tip).css('margin-left', -$tip.outerWidth() / 2).fadeIn(500).delay(2000).fadeOut(500);
        }
        //tip始终显示在屏幕上方top：50px
        function ShowTipAlwaysInTheMiddle(tip,type){
            /*var $left=document.body.clientWidth/2;
            var $top=document.body.clientHeight/2;
            alert($left+","+$top);*/
            var $tip = $('#tip');
            if ($tip.length == 0) {
                $tip = $('<span id="tip" style="font-weight:bold;position:fixed;top:50px;left:50%;z-index:9999"></span>');
                $('body').append($tip);
            }
            $tip.stop(true).attr('class', 'alert alert-' + type).text(tip).css('margin-left', -$tip.outerWidth() / 2).fadeIn(500).delay(2000).fadeOut(500);
        }
        function ShowMsg(msg) {
            ShowTip(msg, 'info');
        }
        function ShowMiddle(msg){
            ShowTipAlwaysInTheMiddle(msg,'info');
        }
        function ShowSuccess(msg) {
            ShowTip(msg, 'success');
        }

        function ShowFailure(msg) {
            ShowTip(msg, 'danger');
        }

        function ShowWarn(msg, $focus, clear) {
            ShowTip(msg, 'warning');
            if ($focus) $focus.focus();
            if (clear) $focus.val('');
            return false;
        }
    </script>
</head>
<body>
<nav class="navbar navbar-inverse" role="navigation">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="/admin/index">图书管理系统</a>
        </div>
        <div>
            <ul class="nav navbar-nav">
                <li class="active"><a href="/admin/index">书籍管理</a></li>
                <li ><a href="/admin/user">用户管理</a></li>
                <li><a href="/admin/orderList">借阅管理</a></li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <li><a href="/logout"><span class="glyphicon glyphicon-log-in"></span>&nbsp退出</a></li>
            </ul>
        </div>
        <div class="pull-right">
            <form class="navbar-form navbar-left" role="search" action="/admin/queryByName">
                <div class="form-group">
                    <input type="text" name="username" class="form-control" placeholder="请输入用户名">
                </div>
                <button type="submit" class="btn btn-default">搜索</button>
            </form>
        </div>
    </div>
</nav>
<!--时间导航栏-->
<%
    Calendar now = Calendar.getInstance();
    int year = now.get(Calendar.YEAR);
    int month = now.get(Calendar.MONTH) + 1;
    int day = now.get(Calendar.DAY_OF_MONTH);
%>
<ol class="breadcrumb">
    <li><%=year%>
    </li>
    <li><%=month%>
    </li>
    <li class="active"><%=day%>
    </li>
</ol>
</div>

<div class="col-sm-10 col-md-offset-1">
    <div class="well">
        <table class="table table-hover table-bordered table-striped thead-inverse">
            <caption>借阅详情</caption>
            <thead>
            <tr>
                <th>用户名</th>
                <th>图片</th>
                <th>书籍名</th>
                <th>作者</th>
                <th>借阅时间</th>
                <th>应还时间</th>
                <th>状态</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <%
                String username=(String)request.getAttribute("username");
                List<BorrowDTO> borrowDTOList= (List<BorrowDTO>)request.getAttribute("borrowDTOList");
                if (borrowDTOList!=null){
                    for (BorrowDTO borrowDTO:borrowDTOList){
                        if (borrowDTO.getBorrowStatus().equals("VALID")){
            %>
            <tr>
                <td class="col-md-1"><%= username %>
                </td>
                <td class="col-md-1"><img src="<% if (borrowDTO.getBookDTO().getPhotoUrl()!=null) out.println(borrowDTO.getBookDTO().getPhotoUrl()); else out.println("https://video-spring.oss-cn-beijing.aliyuncs.com/timg%20%281%29.gif");%>"></td>
                <td class="col-md-1"><%= borrowDTO.getBookDTO().getBookName()%></td>
                <!--TODO 后台控制字数-->
                <td class="col-md-1"><%= borrowDTO.getBookDTO().getAuthor()%>
                </td>
                <td class="col-md-2"><%= borrowDTO.getBorrowDate() %>
                </td>
                <td class="col-md-2"><%= borrowDTO.getBackDate() %>
                </td>
                <td class="col-md-1"><span class="glyphicon glyphicon-remove"></span><%= "未归还" %></td>
                <td class="col-md-1"><span class="glyphicon glyphicon-share"></span> <a href="/admin/updateOrder?borrowId=<%= borrowDTO.getBorrowId() %>"><input type="button" value="还书" class="btn btn-danger"/></a></td>
            </tr>
            <%
                    }
                else {
            %>
            <hr>
            <tr>
                <td class="col-md-1"><%= username %>
                </td>
                <td class="col-md-1"><img src="<% if (borrowDTO.getBookDTO().getPhotoUrl()!=null) out.println(borrowDTO.getBookDTO().getPhotoUrl()); else out.println("https://video-spring.oss-cn-beijing.aliyuncs.com/timg%20%281%29.gif");%>"></td>
                <td class="col-md-1"><%= borrowDTO.getBookDTO().getBookName() %></td>
                <!--TODO 后台控制字数-->
                <td class="col-md-1"><%= borrowDTO.getBookDTO().getAuthor()  %>
                </td>
                <td class="col-md-2"><%= borrowDTO.getBorrowDate() %>
                </td>
                <td class="col-md-2"><%= borrowDTO.getBackDate() %>
                </td>
                <td class="col-md-1"><span class="glyphicon glyphicon-ok"></span><%= "已还" %></td>
                <td class="col-md-1"></td>
            </tr>
            <%
                    }
                }
                }
            %>
            </tbody>

            </body>
</html>
