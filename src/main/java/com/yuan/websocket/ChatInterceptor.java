package com.yuan.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.time.LocalDateTime;
import java.util.Map;

public class ChatInterceptor extends HttpSessionHandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        System.out.println("握手之前");
        System.out.println(request);
        System.out.println(request.getMethodValue());
        System.out.println(request.getBody().toString());
        System.out.println(request.getPrincipal());
        System.out.println(attributes);
        attributes.put("name", LocalDateTime.now().toString());
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
}
