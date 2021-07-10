package plus.messenger.core.services;

import plus.messenger.core.entities.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

public interface MessageStore {

    Mono<Message> storeOne(Message message);

    Flux<Message> store(Collection<Message> messages);

    Mono<Message> getOne(String messageId);

    Flux<Message> get(Collection<String> messageIds);
}
