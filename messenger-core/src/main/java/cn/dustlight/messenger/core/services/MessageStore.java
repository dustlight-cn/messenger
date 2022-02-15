package cn.dustlight.messenger.core.services;

import cn.dustlight.messenger.core.entities.Message;
import cn.dustlight.messenger.core.entities.QueryResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

public interface MessageStore<T extends Message> {

    Mono<T> store(T message);

    Flux<T> store(Collection<T> messages);

    Mono<T> getOne(String messageId, String clientId);

    Mono<Void> markRead(String clientId, Collection<String> ids, String receiver);

    Flux<T> get(Collection<String> messageIds, String clientId);

    Mono<T> update(T message);

    Flux<T> update(Collection<String> messageIds, T update, String clientId);

    Mono<QueryResult<T>> getChat(String clientId, String user, String target, String offset, int size);

    Flux<T> getChatList(String clientId, String user, String offset, int size);
}
