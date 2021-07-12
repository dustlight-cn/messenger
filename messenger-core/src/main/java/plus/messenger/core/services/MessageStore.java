package plus.messenger.core.services;

import plus.messenger.core.entities.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

public interface MessageStore<T extends Message> {

    Mono<T> store(T message);

    Flux<T> store(Collection<T> messages);

    Mono<T> getOne(String messageId);

    Flux<T> get(Collection<String> messageIds);

    Mono<T> update(T message);

    Flux<T> update(Collection<String> messageIds, T update);

    Flux<T> getUnread(String clientId, String receiver);
}
