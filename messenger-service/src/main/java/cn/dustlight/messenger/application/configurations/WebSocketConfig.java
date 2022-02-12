package cn.dustlight.messenger.application.configurations;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import cn.dustlight.auth.resources.core.TokenService;
import cn.dustlight.messenger.application.services.RabbitWebSocketHandler;
import cn.dustlight.messenger.core.services.MessageStore;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebSocketConfig {

    @Bean
    public RabbitWebSocketHandler rabbitWebSocketHandler(@Autowired ConnectionFactory factory,
                                                         @Autowired TokenService tokenService,
                                                         @Autowired MessageStore messageStore,
                                                         @Autowired RabbitTemplate template) {
        return new RabbitWebSocketHandler(factory, tokenService, messageStore, template);
    }

    @Bean
    public HandlerMapping handlerMapping(@Autowired WebSocketHandler handler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/v1/connection", handler);
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        mapping.setOrder(-1);
        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }


}
