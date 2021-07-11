package plus.messenger.application.services;

import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import plus.messenger.core.entities.BasicMessage;
import plus.messenger.core.entities.Channel;
import plus.messenger.core.services.AbstractMessageService;
import plus.messenger.core.services.ChannelService;
import plus.messenger.core.services.MessageStore;
import reactor.core.publisher.Mono;

@Getter
@Setter
public class RabbitMqMessageService<C extends Channel> extends AbstractMessageService<C> {

    private RabbitTemplate rabbitTemplate;

    public RabbitMqMessageService(MessageStore<BasicMessage> messageStore, ChannelService<C> channelService) {
        super(messageStore, channelService);
    }

    public RabbitMqMessageService(MessageStore<BasicMessage> messageStore, ChannelService<C> channelService, RabbitTemplate rabbitTemplate) {
        super(messageStore, channelService);
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Mono<BasicMessage> doSend(BasicMessage message) {
        return Mono.fromCallable(() -> {
            rabbitTemplate.convertAndSend("/" + message.getClientId() + "/" + message.getReceiver(), message);
            return message;
        });
    }
}
