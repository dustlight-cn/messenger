package cn.dustlight.messenger.core.services;

import cn.dustlight.messenger.core.ErrorEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import cn.dustlight.messenger.core.entities.BasicMessage;
import cn.dustlight.messenger.core.entities.Channel;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

@Getter
@Setter
@AllArgsConstructor
public abstract class AbstractMessageService<C extends Channel> implements MessageService<BasicMessage> {

    protected MessageStore<BasicMessage> messageStore;
    protected ChannelService<C> channelService;

    public abstract Mono<BasicMessage> doSend(BasicMessage message);

    public abstract Flux<BasicMessage> doSend(Collection<BasicMessage> messages);

    @Override
    public Mono<BasicMessage> sendMessage(BasicMessage message) {
        if (!StringUtils.hasText(message.getReceiver()))
            return Mono.error(ErrorEnum.CREATE_RESOURCE_FAILED.details("Receiver is empty!").getException());
        message.setId(null);
        message.setCreatedAt(new Date());
        message.setSentAt(null);
        message.setReadAt(null);
        return messageStore.store(message).flatMap(m -> doSend(m));
    }

    @Override
    public Flux<BasicMessage> sendMessage(BasicMessage message, String channelId) {
        return channelService.getChannel(channelId, message.getClientId())
                .flatMapMany(c -> {
                    HashSet<String> targets = new HashSet<>();
                    if (c.getOwner() != null)
                        targets.addAll(c.getOwner());
                    if (c.getMembers() != null)
                        targets.addAll(c.getMembers());
                    if (targets.size() == 0)
                        return Mono.error(ErrorEnum.CREATE_RESOURCE_FAILED.details("Message target is empty!").getException());
                    Collection<BasicMessage> basicMessages = new HashSet<>(targets.size());
                    for (String receiver : targets) {
                        BasicMessage msg = new BasicMessage();
                        msg.setContent(message.getContent());
                        msg.setSender(message.getSender());
                        msg.setReceiver(receiver);
                        msg.setClientId(message.getClientId());
                        msg.setId(null);
                        msg.setCreatedAt(new Date());
                        basicMessages.add(msg);
                    }
                    return messageStore.store(basicMessages)
                            .collectList()
                            .flatMapMany(msgs -> doSend(msgs));
                });
    }

}
