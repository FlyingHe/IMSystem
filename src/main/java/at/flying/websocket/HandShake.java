package at.flying.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by FlyingHe on 2016/9/11.
 */
@Component("handShake")
public class HandShake implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        /*获取从客户端传来的用户的ID值，此ID值作为存储登录用户的Key*/
        Long id = Long.valueOf(((ServletServerHttpRequest) request).getServletRequest().getParameter("id"));
        /*session.getAttributes()返回的Map就是这里的attributes，
          所以将id存入attributes里,后面再Handler里可以通过session获取到该值
        */
        attributes.put("id", id);
        /*返回true以执行下一个拦截器*/
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception exception) {
    }
}
