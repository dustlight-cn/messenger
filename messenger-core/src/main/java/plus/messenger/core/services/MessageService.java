package plus.messenger.core.services;

import plus.messenger.core.entities.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MessageService<T extends Message> {

    Mono<T> sendMessage(T message);

    Flux<T> sendMessage(T message,String channelId);
}
