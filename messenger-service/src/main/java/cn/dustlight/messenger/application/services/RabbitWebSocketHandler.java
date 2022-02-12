package cn.dustlight.messenger.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.socket.WebSocketSession;
import cn.dustlight.auth.resources.core.AuthPrincipal;
import cn.dustlight.auth.resources.core.TokenService;
import cn.dustlight.messenger.core.entities.BasicMessage;
import cn.dustlight.messenger.core.services.MessageStore;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

public class RabbitWebSocketHandler extends AbstractWebsocketHandler {

    private ConnectionFactory factory;
    private RabbitAdmin admin;
    private ObjectMapper objectMapper;
    @Getter
    @Setter
    private MessageStore messageStore;
    @Getter
    @Setter
    private RabbitTemplate rabbitTemplate;

    public RabbitWebSocketHandler(ConnectionFactory connectionFactory,
                                  TokenService tokenService) {
        admin = new RabbitAdmin(connectionFactory);
        this.factory = connectionFactory;
        setTokenService(tokenService);
        objectMapper = new ObjectMapper();
    }

    public RabbitWebSocketHandler(ConnectionFactory connectionFactory,
                                  TokenService tokenService,
                                  MessageStore messageStore,
                                  RabbitTemplate rabbitTemplate) {
        admin = new RabbitAdmin(connectionFactory);
        this.factory = connectionFactory;
        setTokenService(tokenService);
        objectMapper = new ObjectMapper();
        this.messageStore = messageStore;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    protected Mono<Void> map(AuthPrincipal principal, WebSocketSession session) {

        String routingKey = principal.getClientId() + "/" + principal.getUid();
        Queue queue = admin.declareQueue();

        Binding binding = new Binding(queue.getName(),
                Binding.DestinationType.QUEUE,
                rabbitTemplate.getExchange(),
                routingKey,
                null);
        admin.declareBinding(binding);




        // 准备 MessageListenerContainer
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
        simpleMessageListenerContainer.setQueueNames(queue.getActualName());
        simpleMessageListenerContainer.setConnectionFactory(factory);

        // 处理输入
        Mono<Void> input = session.receive()
                .filterWhen(webSocketMessage -> {
                    String payload = webSocketMessage.getPayloadAsText(StandardCharsets.UTF_8);
                    if (!StringUtils.hasText(payload))
                        return Mono.just(false);
                    String msg = payload;
                    if (StringUtils.hasText(msg)) {
                        return Mono.fromCallable(() -> {
                            if (logger.isDebugEnabled())
                                logger.debug("Consume: " + msg);
                            return true;
                        });
                    } else
                        return Mono.just(false);
                })
                .then();

        // 处理输出
        Mono<Void> output = session.send(Flux.create(emitter -> {
            simpleMessageListenerContainer.setupMessageListener(r_message -> {
                BasicMessage basicMessage = null;
                try {
                    basicMessage = objectMapper.readValue(r_message.getBody(), BasicMessage.class);
                    basicMessage.setSentAt(new Date());

                    BasicMessage update = new BasicMessage();
                    update.setId(basicMessage.getId());
                    update.setSentAt(basicMessage.getSentAt());
                    BasicMessage finalBasicMessage = basicMessage;

                    messageStore.update(update)
                            .doOnError(throwable -> emitter.error((Throwable) throwable))
                            .subscribe(m -> {
                                try {
                                    emitter.next(session.textMessage(objectMapper.writeValueAsString(finalBasicMessage)));
                                } catch (Throwable e) {
                                    emitter.error(e);
                                }

                            });
                } catch (Throwable e) {
                    emitter.error(e);
                }

            });
            emitter.onRequest(value -> {
                simpleMessageListenerContainer.start();
                messageStore.getUnread(principal.getClientId(),principal.getUidString())
                        .collectList()
                        .subscribe(tmp -> {
                            List<BasicMessage> messages = (List<BasicMessage>) tmp;
                            for(BasicMessage message : messages){
                                try {
                                    message.setSentAt(new Date());

                                    BasicMessage update = new BasicMessage();
                                    update.setId(message.getId());
                                    update.setSentAt(message.getSentAt());
                                    BasicMessage finalBasicMessage = message;

                                    messageStore.update(update)
                                            .doOnError(throwable -> emitter.error((Throwable) throwable))
                                            .subscribe(m -> {
                                                try {
                                                    emitter.next(session.textMessage(objectMapper.writeValueAsString(finalBasicMessage)));
                                                } catch (Throwable e) {
                                                    emitter.error(e);
                                                }
                                            });

                                } catch (Throwable e) {
                                    emitter.error(e);
                                }
                            }
                        });
            });
            emitter.onDispose(() -> {
                simpleMessageListenerContainer.stop();
            });
        }));
        return Mono.zip(input, output).then();
    }
}
