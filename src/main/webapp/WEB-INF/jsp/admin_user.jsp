<%@ page import="java.util.Calendar" %><%--
  Created by IntelliJ IDEA.
  User: SONG
  Date: 2018/4/15
  Time: 21:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="book.domain.dto.UserDTO" %>
<%@ page import="java.util.List" %>
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
        function ShowTipAlwaysInTheMiddle(tip, type) {
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

        function ShowMiddle(msg) {
            ShowTipAlwaysInTheMiddle(msg, 'info');
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
                <li><a href="/admin/user">用户管理</a></li>
                <li><a href="/admin/orderList">订阅管理</a></li>
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
        <table class="table table-hover table-bordered table-striped">
            <caption>用户个人信息
                <div class="pull-right"><input type="button" value="新增用户信息" class="btn btn-success" data-toggle="modal"
                                               data-target="#myModal"></div>
            </caption>
            <thead>
            <tr>
                <th>用户名</th>
                <th>邮箱</th>
                <th>手机号</th>
                <th>修改人</th>
                <th>创建时间</th>
                <th>最后修改时间</th>
                <th>操作类型</th>
            </tr>
            </thead>
            <tbody>
            <%
                List<UserDTO> userDTOList =(List<UserDTO>)request.getAttribute("userList");
                if (userDTOList != null) {
                    for (UserDTO userDTO : userDTOList) {
            %>
            <tr>
                <td class="col-md-1"><%= userDTO.getUsername()%>
                </td>
                <td class="col-md-1"><% if (userDTO.getEmail()!=null) out.println(userDTO.getEmail()); %>
                </td>
                <!--TODO 后台控制字数-->
                <td class="col-md-3"><% if (userDTO.getPhoneNumber() != null) out.println(userDTO.getPhoneNumber());%>
                </td>
                <td class="col-md-1"><% if (userDTO.getModifier() != null) out.println(userDTO.getModifier()); %>
                </td>
                <td class="col-md-1"><%= userDTO.getGmtCreate() %>
                </td>
                <td class="col-md-1"><%= userDTO.getGmtModified() %>
                </td>
                <td class="col-md-2"><a href="/admin/delete/user/<%=userDTO.getUserId()%>"><input type="button"
                                                                                                  value="删除"
                                                                                                  class="btn btn-danger"/></a>&nbsp&nbsp<input
                        type="button" value="修改" class="btn btn-info" data-toggle="modal"
                        data-target="#<%=userDTO.getUserId()%>"/>


                    <div class="modal fade" id="<%=userDTO.getUserId()%>" tabindex="-1" role="dialog"
                         aria-labelledby="myModalLabel" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <form class="form" class="form-horizontal" role="form" action="/user/update"
                                      method="post">
                                    <div class="modal-header">
                                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                                            &times;
                                        </button>
                                        <h4 class="modal-title">
                                            修改用户个人信息
                                        </h4>
                                    </div>
                                    <div class="modal-body">
                                        <div class="form-group">
                                            <div class="col-sm-10">
                                                <input class="form-control" name="userId" type="hidden"
                                                       value="<%=userDTO.getUserId()%>">
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label class="col-sm-2 control-label">用户名</label>
                                            <div class="col-sm-10">
                                                <input class="form-control" name="username" placeholder="用户名"
                                                       type="text" value="<%= userDTO.getUsername() %>">
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label class="col-sm-2 control-label">密码</label>
                                            <div class="col-sm-10">
                                                <input class="form-control" name="password" placeholder="密码"
                                                       type="password">
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label class="col-sm-2 control-label">手机号</label>
                                            <div class="col-sm-10">
                                                <input class="form-control" name="phoneNumber" type="text"
                                                       placeholder="手机号">

                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label class="col-sm-2 control-label">邮箱</label>
                                            <div class="col-sm-10">
                                                <input class="form-control" name="email" placeholder="邮箱" type="text">
                                            </div>
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-default" data-dismiss="modal">关闭
                                            </button>
                                            <button type="submit" class="btn btn-primary">
                                                修改
                                            </button>
                                        </div>
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


        <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
             aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form class="form" role="form" class="form-horizontal" action="/register" method="post">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                                &times;
                            </button>
                            <h4 class="modal-title">
                                添加用户信息
                            </h4>
                        </div>
                        <div class="modal-body">
                            <div class="form-group">
                                <label class="col-sm-2 control-label">用户名</label>
                                <div class="col-sm-10">
                                    <input class="form-control" name="username" placeholder="用户名" type="text">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">密码</label>
                                <div class="col-sm-10">
                                    <input class="form-control" name="password" type="password" placeholder="密码">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">邮箱</label>
                                <div class="col-sm-10">
                                    <input class="form-control" name="email" placeholder="邮箱" type="text">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">手机</label>
                                <div class="col-sm-10">
                                    <input class="form-control" name="phoneNumber" placeholder="手机" type="text">
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
</div>

</body>
</html>
