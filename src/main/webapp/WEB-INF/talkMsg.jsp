<%--
  Created by IntelliJ IDEA.
  User: FlyingHe
  Date: 2016/9/18
  Time: 18:36
--%>
<%@ page language = "java" pageEncoding = "UTF-8" %>
<%@taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "f" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang = "en">
<head>
    <meta charset = "UTF-8">
    <title></title>
</head>
<body>
    <center><h4 style = "padding: 0;margin: 0">您正在与 ${to.username} 通信</h4>
    </center>
    <hr />
    <div class = "msg_container" style = "height: 350px;overflow: auto">
        <ul id = "talker${to.id}">
            <c:forEach items = "${talkMsgs}" var = "talkMsg">
                <c:choose>
                    <c:when test = "${from.id ne talkMsg.from}">
                        <li class = "qipao">
                            <div class = "headimg fl">
                                <img src = "/IMSystem${from.headImg}" />
                            </div>
                            <div class = "leftqipao">
                                <div class = "left_sj"></div>
                                <div class = "left_con">
                                <span class = "show_time"><f:formatDate pattern = "yyyy-MM-dd HH:mm:ss"
                                                                        value = "${talkMsg.date}" /></span><br />
                                    <hr />
                                        ${talkMsg.content}
                                </div>
                            </div>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class = "qipao">
                            <div class = "headimg fr">
                                <img src = "/IMSystem${from.headImg}" />
                            </div>
                            <div class = "rightqipao">
                                <div class = "right_sj"></div>
                                <div class = "right_con">
                                <span class = "show_time"><f:formatDate pattern = "yyyy-MM-dd HH:mm:ss"
                                                                        value = "${talkMsg.date}" /></span><br />
                                    <hr />
                                        ${talkMsg.content}
                                </div>
                            </div>

                        </li>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </ul>
    </div>
    <div>
        <textarea id = "talker_msg${to.id}" style = "width:100%;height:100px;"></textarea>
        <center>
            <button onclick = "sendMsg()">发送</button>
            ${to.id eq 1 ? "(按Enter键即可发送信息)" : ""}
        </center>
    </div>
</body>
</html>