package cn.dustlight.messenger.core.services;

import cn.dustlight.messenger.core.entities.Notification;
import reactor.core.publisher.Mono;

public interface NotificationRecordStore<T extends Notification> {

    Mono<T> store(T notification);

    Mono<T> get(String id);

    Mono<Void> remove(String id);

}
