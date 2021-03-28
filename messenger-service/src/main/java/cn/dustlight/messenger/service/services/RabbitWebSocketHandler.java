package cn.dustlight.messenger.service.services;

import cn.dustlight.messenger.core.AbstractWebsocketHandler;
import cn.dustlight.messenger.core.Message;
import cn.dustlight.messenger.core.MessengerPrincipal;
import cn.dustlight.messenger.core.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RabbitWebSocketHandler extends AbstractWebsocketHandler {

    private ConnectionFactory factory;
    private RabbitAdmin admin;
    private ObjectMapper objectMapper;

    public RabbitWebSocketHandler(ConnectionFactory connectionFactory,
                                  TokenService tokenService) {
        admin = new RabbitAdmin(connectionFactory);
        this.factory = connectionFactory;
        setTokenService(tokenService);
        objectMapper = new ObjectMapper();
    }

    @Override
    protected Mono<Void> map(MessengerPrincipal principal, WebSocketSession session) {
        // 准备 Exchange
        Exchange exchange = new DirectExchange("client-id:" + principal.getClientId(),
                false,
                true);
        admin.declareExchange(exchange);

        // 准备 Queue
        Queue queue = admin.declareQueue();

        // 准备 Binding
        Binding binding = new Binding(queue.getActualName(),
                Binding.DestinationType.QUEUE, exchange.getName(),
                session.getHandshakeInfo().getUri().getPath(),
                null);
        admin.declareBinding(binding);

        // 准备 Template
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setExchange(exchange.getName());
        template.setRoutingKey(binding.getRoutingKey());

        // 准备 MessageListenerContainer
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
        simpleMessageListenerContainer.setQueueNames(queue.getName());
        simpleMessageListenerContainer.setConnectionFactory(factory);

        String uid = principal.getUid().toString();
        Message message = new Message();
        message.setUid(uid);

        // 处理输入
        Mono<Void> input = session.receive()
                .filterWhen(webSocketMessage -> {
                    String payload = webSocketMessage.getPayloadAsText(StandardCharsets.UTF_8);
                    if (!StringUtils.hasText(payload))
                        return Mono.just(false);
                    String msg;
                    try {
                        Map data = objectMapper.readValue(payload, HashMap.class);
                        message.setData(data);
                        msg = objectMapper.writeValueAsString(message);
                    } catch (JsonProcessingException e) {
                        logger.debug(e.getMessage(), e);
                        return Mono.just(false);
                    }
                    if (StringUtils.hasText(msg)) {
                        return Mono.fromCallable(() -> {
                            if (logger.isDebugEnabled())
                                logger.debug("Consume: " + msg);
                            template.convertAndSend(msg);
                            return true;
                        });
                    } else
                        return Mono.just(false);
                })
                .then();

        // 处理输出
        Mono<Void> output = session.send(Flux.create(emitter -> {
            simpleMessageListenerContainer.setupMessageListener(r_message -> {
                String msg = new String(r_message.getBody());
                if (logger.isDebugEnabled())
                    logger.debug("Produce: " + msg);
                emitter.next(session.textMessage(msg));
            });
            emitter.onRequest(value -> {
                simpleMessageListenerContainer.start();
            });
            emitter.onDispose(() -> {
                simpleMessageListenerContainer.stop();
            });
        }));
        return Mono.zip(input, output).then();
    }
}
