package plus.messenger.core.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import plus.messenger.core.entities.BasicMessage;
import plus.messenger.core.entities.Channel;
import plus.messenger.core.entities.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

@Getter
@Setter
@AllArgsConstructor
public abstract class AbstractMessageService<C extends Channel> implements MessageService<BasicMessage> {

    private MessageStore<BasicMessage> messageStore;
    private ChannelService<C> channelService;

    public abstract Mono<BasicMessage> doSend(BasicMessage message);

    @Override
    public Mono<BasicMessage> sendMessage(BasicMessage message) {
        message.setId(null);
        message.setCreatedAt(new Date());
        return messageStore.storeOne(message).flatMap(m -> doSend(m));
    }

    @Override
    public Flux<BasicMessage> sendMessage(BasicMessage message, String channelId) {
        return Flux.create(emitter -> {
            System.out.println("?");
            emitter.onRequest(value -> {
                System.out.println(channelId);
                channelService.getChannel(channelId)
                        .map(c -> {
                            System.out.println("?");
                            System.out.println(c);
                            HashSet<String> targets = new HashSet<>();
                            if (c.getOwner() != null)
                                targets.addAll(c.getOwner());
                            if (c.getMembers() != null)
                                targets.addAll(c.getMembers());
                            Mono<BasicMessage> tmp = null;
                            for (String id : targets) {
                                BasicMessage msg = new BasicMessage();
                                msg.setContent(message.getContent());
                                msg.setSender(message.getSender());
                                msg.setReceiver(id);
                                msg.setClientId(message.getClientId());
                                if (tmp == null)
                                    tmp = sendMessage(msg);
                                else
                                    tmp = tmp.doOnNext(m -> sendMessage(msg));
                                tmp.doOnSuccess(m -> {
                                    emitter.next(m);
                                })
                                        .doOnError(throwable -> emitter.error(throwable));
                            }
                            if (tmp != null)
                                tmp.doOnNext(m -> emitter.complete());
                            else emitter.complete();
                            return Mono.empty();
                        })
                        .doOnError(throwable -> {
                            System.out.println(throwable);
                            emitter.error(throwable);
                        });
            });
        });
    }

}
