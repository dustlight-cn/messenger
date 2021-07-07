package plus.messenger.core.services;

import plus.messenger.core.entities.Message;
import reactor.core.publisher.Mono;

/**
 * 消息发送器
 */
public interface MessageTransmitter {

    String getName();

    Mono<Void> send(Message message);
}
