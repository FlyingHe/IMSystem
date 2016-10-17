package at.flying.websocket;

import at.flying.domain.MsgType;
import at.flying.domain.TalkMsg;
import at.flying.service.TalkMsgService;
import at.flying.service.TalkerService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

/**
 * Created by FlyingHe on 2016/9/16.
 */
@Component("talkWebSoketHandler")
public class TalkWebSoketHandler extends TextWebSocketHandler {
    @Autowired
    private TalkMsgService talkMsgService;
    @Autowired
    private TalkerService talkerService;
    /*存储建立的所有连接*/
    private static Map<Long, WebSocketSession> sessions = new HashMap<>();
    /*用于处理Json数据*/
    private static ObjectMapper objectMapper = new ObjectMapper();

    /*获取当前建立的所有连接*/
    public static Map<Long, WebSocketSession> getSessions() {
        return sessions;
    }


    /*客户端与服务器端连接建立之后执行的函数*/
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("afterConnectionEstablished");
        if (session.getAttributes().get("id") == null) {
            session.close();
            return;
        }
        /*获取当前用户的ID*/
        Long id = (Long) session.getAttributes().get("id");
        /*存储当前用户建立的链接，即会话session*/
        sessions.put(id, session);
        System.out.println("用户" + id + "一上线");
        /*通知所有登录用户客户端更新在线列表*/
        this.updateTalkersOnline();
    }

    /*客户端向服务器发送信息时会调用该函数*/
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        /*获取信息内容*/
        String jsonstr = message.getPayload();
        /*获取该信息类型以选择处理该信息的方式*/
        String type = objectMapper.readTree(jsonstr).get("type").asText();
        if (type.equalsIgnoreCase(MsgType.LOGOFF_TALKER)) {
            /*此类型表示该用户要下线*/
            this.userLogoff(session, message);
        } else if (type.equalsIgnoreCase(MsgType.SEND_MESSAGE)) {
            /*此类型表示该用户要发送信息给别人*/
            this.sendMsg(session, message);
        } else if (type.equalsIgnoreCase(MsgType.SEND_MESSAGE_TO_Robot)) {
            /*此类表示要将信息发送给机器人*/
            this.sendMsgToRobot(session, message);
        }
    }

    /*连接发生异常时执行的函数*/
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        this.removeAndCloseSession(null, session);
    }

    /*连接关闭后执行的函数*/
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        this.removeAndCloseSession(null, session);
        /*用户下线时，通知所有登录用户客户端更新在线列表*/
        this.updateTalkersOnline();
    }

    /*移除并关闭session*/
    private void removeAndCloseSession(Long id, WebSocketSession session) throws IOException {
        if (id != null && sessions.containsKey(id)) {
            WebSocketSession s = sessions.remove(id);
            if (s.isOpen()) {
                s.close();
            }
        } else {
            Long idd = null;
            if (session.getAttributes().get("id") instanceof Long) {
                idd = (Long) session.getAttributes().get("id");
            }
            if (idd != null && sessions.containsKey(idd)) {
                WebSocketSession s = sessions.remove(idd);
                if (s.isOpen()) {
                    s.close();
                }
            } else {
                if (session.isOpen()) {
                    session.close();
                }
            }
        }
    }

    /*用户发送信息执行的函数*/
    private void sendMsg(WebSocketSession session, TextMessage message) throws IOException {
        /*将信息内容封装到信息对象中*/
        TalkMsg talkMsg = objectMapper
                .readValue(objectMapper.readTree(message.getPayload()).get("msg").toString(), TalkMsg.class);
        talkMsg.setDate(new Date());
        /*将该信息存入数据库*/
        this.talkMsgService.add(talkMsg);
//        System.out.println("ID:" + talkMsg.getId());
        /*获取目标用户的session*/
        WebSocketSession to_session = sessions.get(talkMsg.getTo());
        /*将信息转成Json字符串*/
        String json_msg = objectMapper.writeValueAsString(talkMsg);
        /*通知当前用户信息发送成功*/
        if (session.isOpen()) {
            session.sendMessage(
                    new TextMessage("{\"type\":\"" + MsgType.SEND_MESSAGE_SUCCESSFUL + "\",\"msg\":" + json_msg + "}"));
        }
        /*如果目标用户在线的话就将该信息发送给目标用户*/
        if (to_session != null && to_session.isOpen()) {
            to_session.sendMessage(
                    new TextMessage("{\"type\":\"" + MsgType.SEND_MESSAGE + "\",\"msg\":" + json_msg + "}"));
        }
    }

    /*用户下线执行函数*/
    private void userLogoff(WebSocketSession session, TextMessage message) throws Exception {
        String jsonstr = message.getPayload();
        JsonNode root = objectMapper.readTree(jsonstr);
        if (root.get("logoffUser") != null) {
            if (root.get("logoffUser").asBoolean()) {
                Long id = root.get("id").asLong();
                this.removeAndCloseSession(id, session);
                System.out.println("用户" + id + "已下线");
            }
        }
    }

    /*服务器提醒所有在线用户，更新客户端在线列表*/
    private void updateTalkersOnline() {
        /*采用多线程在在线人数比较多的情况下提高执行效率*/
        for (Map.Entry<Long, WebSocketSession> entry : sessions.entrySet()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    WebSocketSession session = entry.getValue();
                    if (session.isOpen()) {
                        try {
                            session.sendMessage(new TextMessage(
                                    "{\"type\":\"" + MsgType.UPDATE_TALKERS_ONLINE +
                                            "\",\"updateTalkersOnline\":true}"));
                        } catch (IOException e) {
                            try {
                                removeAndCloseSession(null, session);
                            } catch (IOException e1) {
                            }
                        }
                    }
                }
            }).start();

        }
    }

    /*将信息发送给机器人*/
    private void sendMsgToRobot(WebSocketSession session, TextMessage message) throws IOException {
        /*将信息内容封装到信息对象中*/
        TalkMsg talkMsg_receive = objectMapper
                .readValue(objectMapper.readTree(message.getPayload()).get("msg").toString(), TalkMsg.class);
        talkMsg_receive.setDate(new Date());
        /*与机器人通信时发送的Http请求*/
        HttpClient httpClient = new HttpClient();
        // 创建post请求,类似Post请求
        PostMethod postMethod =
                new PostMethod("http://www.tuling123.com/openapi/api");
        // 设置请求的正文内容
        String json_text = "{\"key\":\"ddb6c58300394fdfbf9101ba9e3bc8a8\",\"info\":\"" + talkMsg_receive.getContent() +
                "\",\"userid\":\"" + talkMsg_receive.getFrom() + "\"}";
        StringRequestEntity stringRequestEntity =
                new StringRequestEntity(json_text, "application/json", "UTF-8");
        postMethod.setRequestEntity(stringRequestEntity);
        /*通知当前用户信息发送成功*/
        if (session.isOpen()) {
            session.sendMessage(
                    new TextMessage("{\"type\":\"" + MsgType.SEND_MESSAGE_SUCCESSFUL + "\",\"msg\":" +
                            objectMapper.writeValueAsString(talkMsg_receive) + "}"));
        }
        // 发送post请求
        httpClient.executeMethod(postMethod);
        //获取响应结果
        String result = new String(postMethod.getResponseBodyAsString().getBytes("ISO-8859-1"), "UTF-8");
         /*将机器人返回的信息封装并转成Json字符串*/
        TalkMsg talkMsg_send = new TalkMsg();
        talkMsg_send.setFrom(1L);
        talkMsg_send.setTo(talkMsg_receive.getFrom());
        talkMsg_send.setContent(this.parseTuringData(result));
        talkMsg_send.setDate(new Date());
        String json_msg = objectMapper.writeValueAsString(talkMsg_send);

        /*推送图灵机器人消息给此用户*/
        if (session.isOpen()) {
            session.sendMessage(
                    new TextMessage("{\"type\":\"" + MsgType.SEND_MESSAGE + "\",\"msg\":" + json_msg + "}"));
        }
        //释放与图灵的HTTP连接
        postMethod.releaseConnection();
    }

    /*解析从图灵请求来的Json数据*/
    private String parseTuringData(String turingResult) throws IOException {
        int code = objectMapper.readTree(turingResult).get("code").asInt();
        String content = null;
        switch (code) {
            case 200000:
                content = this.turing_200000(turingResult);
                break;
            case 302000:
                content = this.turing_302000(turingResult);
                break;
            case 308000:
                content = this.turing_308000(turingResult);
                break;
            default:
                content = this.turing_text(turingResult);
                break;
        }

        return content;
    }

    /*图灵请求code=308000的处理*/
    private String turing_308000(String turingResult) throws IOException {
        StringBuilder content = new StringBuilder();
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        List<Map<String, String>> news = objectMapper
                .readValue(objectMapper.readTree(turingResult).get("list").toString(), typeFactory
                        .constructCollectionType(ArrayList.class, typeFactory.constructMapType(
                                HashMap.class, String.class, String.class)));
        String text = this.turing_text(turingResult);
        content.append(text).append("<br/><hr/>");
        for (Map<String, String> map : news) {
            content.append("<a target = \"_blank\" class = \"turing_link\" href = \"").append(map.get("detailurl"))
                    .append("\">").append(map.get("name")).append("<br/>材料:").append(map.get("info")).
                    append("<img src = \"").append(map.get("icon")).
                    append("\"/></a><hr/>");
        }
        content.delete(content.lastIndexOf("<hr/>"), content.length()).insert(0, "<p>")
                .insert(content.length(), "</p>");
        return content.toString();
    }

    /*图灵请求code=302000的处理*/
    private String turing_302000(String turingResult) throws IOException {
        StringBuilder content = new StringBuilder();
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        List<Map<String, String>> news = objectMapper
                .readValue(objectMapper.readTree(turingResult).get("list").toString(), typeFactory
                        .constructCollectionType(ArrayList.class, typeFactory.constructMapType(
                                HashMap.class, String.class, String.class)));
        String text = this.turing_text(turingResult);
        content.append(text).append("<br/><hr/>");
        for (Map<String, String> map : news) {
            content.append("<a target = \"_blank\" class = \"turing_link\" href = \"").append(map.get("detailurl"))
                    .append("\">").append(map.get("article")).append("----来自").append(map.get("source")).
                    append("</a><hr/>");
        }
        content.delete(content.lastIndexOf("<hr/>"), content.length()).insert(0, "<p>")
                .insert(content.length(), "</p>");
        return content.toString();
    }

    /*图灵请求code=200000的处理*/
    private String turing_200000(String turingResult) throws IOException {
        String text = this.turing_text(turingResult);
        String url = objectMapper.readTree(turingResult).get("url").asText();
        return "<p>" + text + "<br/><a class = \"turing_link\" target = \"_blank\" href = \"" + url +
                "\">请点击打开页面</a></p>";
    }

    /*图灵请求错误以及code=100000的处理*/
    private String turing_text(String turingResult) throws IOException {
        return objectMapper.readTree(turingResult).get("text").asText();
    }
}
