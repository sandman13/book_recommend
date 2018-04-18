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
            <a class="navbar-brand" href="#">图书管理系统</a>
        </div>
        <div>
            <ul class="nav navbar-nav">
                <li class="active"><a href="/admin/index">书籍管理</a></li>
                <li ><a href="/admin/user">用户管理</a></li>
                <li><a href="/admin/orderList">订阅管理</a></li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <li><a href="/logout"><span class="glyphicon glyphicon-log-in"></span>&nbsp退出</a></li>
            </ul>
        </div>
        <div class="pull-right">
            <form class="navbar-form navbar-left" role="search" action="/admin/queryBook" >
                <div class="form-group">
                    <input type="text" name="bookName" class="form-control" placeholder="请输入书名">
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


<form class="form-horizontal" role="form" action="/admin/queryByMultiConditions">
    <div class="form-group">
        <label class="col-sm-2 control-label">出版社</label>
        <div class="col-sm-3">
            <input type="text" class="form-control" name="publisher" placeholder="出版社">
        </div>
        <label class="col-sm-2 control-label">作者</label>
        <div class="col-sm-3">
            <input type="text" class="form-control" name="author" placeholder="作者">
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label">介绍</label>
        <div class="col-sm-3">
            <input type="text" class="form-control" name="introduction" placeholder="介绍">
        </div>
        <label class="col-sm-2 control-label">地点</label>
        <div class="col-sm-3">
            <input type="text" class="form-control" name="location" placeholder="地点">
        </div>
        <div class="col-sm-1">
            <button type="submit" class="btn btn-primary">搜索</button>
        </div>
    </div>
</form>

<div class="col-sm-10 col-md-offset-1">
    <div class="well">
        <table class="table table-hover table-bordered table-striped">
            <caption>书籍信息详情<div class="pull-right"><input type="button" value="新增书籍信息" class="btn btn-success" data-toggle="modal" data-target="#myModal"></div></caption>
            <thead>
            <tr>
                <th>书名</th>
                <th>作者</th>
                <th>图片</th>
                <th>介绍</th>
                <th>地点</th>
                <th>出版日期</th>
                <th>出版社</th>
                <th>操作类型</th>
            </tr>
            </thead>
            <tbody>
            <%
                PageInfo<BookDTO> pageInfo = (PageInfo<BookDTO>) request.getAttribute("pageInfo");
                String introduction=(String)request.getAttribute("introduction");
                String publisher = (String) request.getAttribute("publisher");
                String location=(String) request.getAttribute("location");
                String author=(String) request.getAttribute("author");
                if (pageInfo!=null){
                    for (BookDTO bookDTO:pageInfo.getList()){

            %>
            <tr>
                <td class="col-md-1"><%= bookDTO.getBookName()%>
                </td>
                <td class="col-md-1"><%= bookDTO.getAuthor()%>
                </td>
                <td class="col-md-1"><img src="<% if (bookDTO.getPhotoUrl()!=null) out.println(bookDTO.getPhotoUrl()); else out.println("https://video-spring.oss-cn-beijing.aliyuncs.com/timg%20%281%29.gif");%>"></td>
                <!--TODO 后台控制字数-->
                <td class="col-md-3"><% if  (bookDTO.getIntroduction()!=null) out.println(bookDTO.getIntroduction()); %>
                </td>
                <td class="col-md-1"><% if (bookDTO.getLocation()!=null) out.println(bookDTO.getLocation());%>
                </td>
                <td class="col-md-1"><% if (bookDTO.getPubdate()!=null) out.println(bookDTO.getPubdate()); %>
                </td>
                <td class="col-md-1"><% if (bookDTO.getPublisher()!=null) out.println(bookDTO.getPublisher()); %></td>
                <td class="col-md-1"><a href="/admin/delete/book/<%=bookDTO.getBookId()%>"><input type="button" value="删除" class="btn btn-danger"/></a><input type="button" value="修改" class="btn btn-info"  data-toggle="modal" data-target="#<%=bookDTO.getBookId()%>"/>


                    <div class="modal fade" id="<%=bookDTO.getBookId()%>" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <form class="form"  class="form-horizontal" role="form" action="/admin/update" method="post" enctype="multipart/form-data">
                                    <div class="modal-header">
                                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                                            &times;
                                        </button>
                                        <h4 class="modal-title" >
                                            修改书籍信息
                                        </h4>
                                    </div>
                                    <div class="modal-body">
                                        <div class="form-group">
                                            <div class="col-sm-10">
                                                <input class="form-control"  name="bookId" type="hidden" value="<%=bookDTO.getBookId()%>">
                                            </div>
                                        </div>
                                        <div class="form-group" >
                                            <label class="col-sm-2 control-label">书名</label>
                                            <div class="col-sm-10">
                                                <input class="form-control"  name="bookName" placeholder="书名" type="text">
                                            </div>
                                        </div>
                                        <div class="form-group" >
                                            <label  class="col-sm-2 control-label">作者</label>
                                            <div class="col-sm-10">
                                                <input class="form-control" name="author" type="text" placeholder="作者">
                                            </div>
                                        </div>
                                        <div class="form-group" >
                                            <label class="col-sm-2 control-label">介绍</label>
                                            <div class="col-sm-10">
                                                <input class="form-control" name="introduction" placeholder="介绍" type="text">
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label class="col-sm-2 control-label">地点</label>
                                            <div class="col-sm-10">
                                                <input class="form-control" name="location" placeholder="地点" type="text">
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label class="col-sm-2 control-label">出版日期</label>
                                            <div class="col-sm-10">
                                                <input class="form-control" name="pubdate" placeholder="出版日期" type="text">
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label class="col-sm-2 control-label">出版商</label>
                                            <div class="col-sm-10">
                                                <input class="form-control" name="publisher" placeholder="出版商" type="text">
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label class="col-sm-2 control-label">图片</label>
                                            <div class="col-sm-10">
                                                <input class="form-control" name="multipartFile" placeholder="书籍图片" type="file">
                                            </div>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-default" data-dismiss="modal">关闭
                                        </button>
                                        <button type="submit" class="btn btn-primary">
                                            修改
                                        </button>
                                    </div>
                                </form>
                            </div><!-- /.modal-content -->
                        </div><!-- /.modal -->
                    </div>


            </tr>

            <%
                    }
                }
            %>
            </tbody>
        </table>


        <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form class="form" role="form"  class="form-horizontal" action="/admin/insert" method="post" enctype="multipart/form-data">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                                &times;
                            </button>
                            <h4 class="modal-title" >
                                添加书籍信息
                            </h4>
                        </div>
                        <div class="modal-body">
                            <div class="form-group">
                                <label class="col-sm-2 control-label">书名</label>
                                <div class="col-sm-10">
                                    <input class="form-control"  name="bookName" placeholder="书名" type="text">
                                </div>
                            </div>
                            <div class="form-group">
                                <label  class="col-sm-2 control-label">作者</label>
                                <div class="col-sm-10">
                                    <input class="form-control" name="author" type="text" placeholder="作者">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">介绍</label>
                                <div class="col-sm-10">
                                    <input class="form-control" name="introduction" placeholder="介绍" type="text">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">地点</label>
                                <div class="col-sm-10">
                                    <input class="form-control" name="location" placeholder="地点" type="text">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">出版日期</label>
                                <div class="col-sm-10">
                                    <input class="form-control" name="pubdate" placeholder="出版日期" type="text">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">出版商</label>
                                <div class="col-sm-10">
                                    <input class="form-control" name="publisher" placeholder="出版商" type="text">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">图片</label>
                                <div class="col-sm-10">
                                    <input class="form-control" name="multipartFile" placeholder="书籍图片" type="file">
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal">关闭
                            </button>
                            <button type="submit" class="btn btn-primary">
                                添加
                            </button>
                        </div>
                    </form>
                </div><!-- /.modal-content -->
            </div><!-- /.modal -->
        </div>







    </div>
    <!--分页-->
    <nav aria-label="Page navigation">
        <ul class="pagination">
            <li>
                <a href="/admin/queryByMultiConditions?page=<%= pageInfo.getPrePage() %>&publisher=<%=publisher%>&location=<%=location%>&introduction=<%=introduction%>&author=<%=author%>" aria-label="Previous">
                    <span aria-hidden="true">&laquo;</span>
                </a>
            </li>
            <% for (int i=0;i<pageInfo.getPages();i++){
                if (pageInfo.getPageNum()==i+1){
            %>
            <li class="active"><a href="/admin/queryByMultiConditions?page=<%= i+1 %>&publisher=<%=publisher%>&location=<%=location%>&introduction=<%=introduction%>&author=<%=author%>"><%= i+1 %></a></li>
            <%}else {%>
            <li><a href="/admin/queryByMultiConditions?page=<%= i+1 %>&publisher=<%=publisher%>&location=<%=location%>&introduction=<%=introduction%>&author=<%=author%>"><%= i+1 %></a></li>
            <% }} %>
            <li>
                <a href="/admin/queryByMultiConditions?page=<%= pageInfo.getNextPage()%>&publisher=<%=publisher%>&location=<%=location%>&introduction=<%=introduction%>&author=<%=author%>" aria-label="Next">
                    <span aria-hidden="true">&raquo;</span>
                </a>
            </li>
        </ul>
    </nav>

</div>

</body>
</html>
