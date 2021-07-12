package plus.messenger.application.services;

import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import plus.messenger.core.entities.BasicMessage;
import plus.messenger.core.entities.Channel;
import plus.messenger.core.services.AbstractMessageService;
import plus.messenger.core.services.ChannelService;
import plus.messenger.core.services.MessageStore;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.HashMap;

@Getter
@Setter
public class RabbitMqMessageService<C extends Channel> extends AbstractMessageService<C> {

    private RabbitTemplate rabbitTemplate;
    private RabbitAdmin rabbitAdmin;

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

            String queueName = message.getClientId() + "/" + message.getReceiver();
            String routingKey = message.getClientId() + "/" + message.getReceiver();

            var args = new HashMap<String, Object>();
            args.put("x-max-priority", 10);
            args.put("x-max-length", 1024 * 1024);
            Queue queue = new Queue(queueName,
                    true,
                    false,
                    false,
                    args);
            rabbitAdmin.declareQueue(queue);

            Binding binding = new Binding(queue.getName(),
                    Binding.DestinationType.QUEUE,
                    rabbitTemplate.getExchange(),
                    routingKey,
                    null);

            rabbitAdmin.declareBinding(binding);
            rabbitTemplate.convertAndSend(binding.getRoutingKey(), message);
            return message;
        });
    }

    @Override
    public Flux<BasicMessage> doSend(Collection<BasicMessage> messages) {
        return Flux.create(basicMessageFluxSink -> {
            basicMessageFluxSink.onRequest(value -> {
                try {
                    for (BasicMessage message : messages) {
                        String queueName = message.getClientId() + "/" + message.getReceiver();
                        String routingKey = message.getClientId() + "/" + message.getReceiver();

                        var args = new HashMap<String, Object>();
                        args.put("x-max-priority", 10);
                        args.put("x-max-length", 1024 * 1024);
                        Queue queue = new Queue(queueName,
                                true,
                                false,
                                false,
                                args);
                        rabbitAdmin.declareQueue(queue);

                        Binding binding = new Binding(queue.getName(),
                                Binding.DestinationType.QUEUE,
                                rabbitTemplate.getExchange(),
                                routingKey,
                                null);

                        rabbitAdmin.declareBinding(binding);
                        rabbitTemplate.convertAndSend(binding.getRoutingKey(), message);
                        basicMessageFluxSink.next(message);
                    }
                    basicMessageFluxSink.complete();
                } catch (Throwable throwable) {
                    for (BasicMessage message : messages) {
                        String queueName = message.getClientId() + "/" + message.getReceiver();
                        String routingKey = message.getClientId() + "/" + message.getReceiver();

                        var args = new HashMap<String, Object>();
                        args.put("x-max-priority", 10);
                        args.put("x-max-length", 1024 * 1024);
                        Queue queue = new Queue(queueName,
                                true,
                                false,
                                false,
                                args);
                        rabbitAdmin.declareQueue(queue);

                        Binding binding = new Binding(queue.getName(),
                                Binding.DestinationType.QUEUE,
                                rabbitTemplate.getExchange(),
                                routingKey,
                                null);

                        rabbitAdmin.declareBinding(binding);
                        rabbitTemplate.convertAndSend(binding.getRoutingKey(), message);
                        basicMessageFluxSink.next(message);
                    }
                    basicMessageFluxSink.error(throwable);
                }

            });
        });
    }
}
