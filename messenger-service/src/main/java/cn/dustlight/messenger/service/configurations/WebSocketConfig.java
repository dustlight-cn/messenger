package cn.dustlight.messenger.service.configurations;

import cn.dustlight.messenger.core.TokenService;
import cn.dustlight.messenger.service.services.RabbitWebSocketHandler;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebSocketConfig {

    @Bean
    public RabbitWebSocketHandler rabbitWebSocketHandler(@Autowired ConnectionFactory factory,
                                                         @Autowired TokenService tokenService) {
        return new RabbitWebSocketHandler(factory, tokenService);
    }

    @Bean
    public HandlerMapping handlerMapping(@Autowired WebSocketHandler handler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/**", handler);
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
