package plus.messenger.core.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import plus.messenger.core.entities.BasicMessage;
import plus.messenger.core.entities.Channel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

@Getter
@Setter
@AllArgsConstructor
public abstract class AbstractMessageService<C extends Channel> implements MessageService<BasicMessage> {

    private MessageStore<BasicMessage> messageStore;
    private ChannelService<C> channelService;

    public abstract Mono<BasicMessage> doSend(BasicMessage message);

    public abstract Flux<BasicMessage> doSend(Collection<BasicMessage> messages);

    @Override
    public Mono<BasicMessage> sendMessage(BasicMessage message) {
        message.setId(null);
        message.setCreatedAt(new Date());
        return messageStore.storeOne(message).flatMap(m -> doSend(m));
    }

    @Override
    public Flux<BasicMessage> sendMessage(BasicMessage message, String channelId) {
        return channelService.getChannel(channelId)
                .flux()
                .flatMap(c -> {
                    HashSet<String> targets = new HashSet<>();
                    if (c.getOwner() != null)
                        targets.addAll(c.getOwner());
                    if (c.getMembers() != null)
                        targets.addAll(c.getMembers());

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
                            .flux()
                            .flatMap(msgs -> doSend(msgs));
                });
    }

}
