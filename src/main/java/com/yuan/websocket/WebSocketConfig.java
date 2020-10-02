package com.yuan.websocket;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * https://juejin.im/post/6844903908939137037
 * 1. 第一步编写配置类
 * 2. 编写消息模型用来交互
 * 3. 编写控制器处理用户的请求
 * 4. 编写监听器监听用户连接与断开
 * 5. 前端使用stomp.js Client连接
 * 6. 对stompClient和按钮绑定相应事件
 * <p>
 * 还没学如何根据token进行拦截,这个demo网上复制下来逐行学习的.所以就都放一起了
 * https://blog.csdn.net/ch999999999999999999/article/details/102635322/
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    /**
     * 注册端点
     * <p>
     * /ws是 WebSocket(或 SockJS)客户端为 WebSocket 握手需要连接的端点的 HTTP URL。
     * 例:ws://localhost:8080/ws
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 别给我折叠了 setHandshakeHandler()不是很懂咋用
        registry.addEndpoint("/ws").setAllowedOrigins("*").addInterceptors(new ChatInterceptor());
    }

    /**
     * 配置消息代理
     * 设置应用端点前缀
     * ----目标头以/app开头的 STOMP 消息将路由到控制器类中的@MessageMapping方法。
     * 启用简单的代理.代理/topic 进行广播  /queue 进行信息交换
     * ----使用内置的消息代理进行订阅,并使用 `广播` 和 `路由` 将目标标题
     * ----以`/topic`或`/queue`开头的消息发送到 broker。
     * ----topic和/queue前缀没有任何特殊含义。它们只是区分 pub-sub 与 point-to-point 消息传递的惯例(即许多订阅者与一个消费者)
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        config.enableSimpleBroker("/topic", "/queue");
    }
}

