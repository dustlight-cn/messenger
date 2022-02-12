package cn.dustlight.messenger.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import cn.dustlight.messenger.core.entities.BasicMessage;
import cn.dustlight.messenger.core.entities.Channel;
import cn.dustlight.messenger.core.services.AbstractMessageService;
import cn.dustlight.messenger.core.services.ChannelService;
import cn.dustlight.messenger.core.services.MessageStore;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Getter
@Setter
public class RabbitMqMessageService<C extends Channel> extends AbstractMessageService<C> {

    private RabbitTemplate rabbitTemplate;
    private RabbitAdmin rabbitAdmin;
    private ObjectMapper objectMapper = new ObjectMapper();

    public RabbitMqMessageService(MessageStore<BasicMessage> messageStore,
                                  ChannelService<C> channelService) {
        super(messageStore, channelService);
    }

    public RabbitMqMessageService(MessageStore<BasicMessage> messageStore,
                                  ChannelService<C> channelService,
                                  RabbitTemplate rabbitTemplate,
                                  RabbitAdmin rabbitAdmin) {
        super(messageStore, channelService);
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitAdmin = rabbitAdmin;
    }

    @Override
    public Mono<BasicMessage> doSend(BasicMessage message) {
        return Mono.fromCallable(() -> {
            String routingKey = message.getClientId() + "/" + message.getReceiver();
            rabbitTemplate.convertAndSend(routingKey, objectMapper.writeValueAsString(message));
            return message;
        });
    }

    @Override
    public Flux<BasicMessage> doSend(Collection<BasicMessage> messages) {
        return Flux.create(basicMessageFluxSink -> {
            basicMessageFluxSink.onRequest(value -> {
                try {
                    Date t = new Date();
                    for (BasicMessage message : messages) {
                        String routingKey = message.getClientId() + "/" + message.getReceiver();
                        rabbitTemplate.convertAndSend(routingKey, objectMapper.writeValueAsString(message));
                        basicMessageFluxSink.next(message);
                    }
                    basicMessageFluxSink.complete();
                } catch (Throwable throwable) {
                    basicMessageFluxSink.error(throwable);
                }
            });
        });
    }
}
