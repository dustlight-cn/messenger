package cn.dustlight.messenger.core.services;

import cn.dustlight.messenger.core.entities.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

public interface MessageStore<T extends Message> {

    Mono<T> store(T message);

    Flux<T> store(Collection<T> messages);

    Mono<T> getOne(String messageId,String clientId);

    Flux<T> get(Collection<String> messageIds,String clientId);

    Mono<T> update(T message);

    Flux<T> update(Collection<String> messageIds, T update,String clientId);

    Flux<T> getUnread(String clientId, String receiver);
}
