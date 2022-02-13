package cn.dustlight.messenger.core.services;

import cn.dustlight.messenger.core.entities.Notification;
import cn.dustlight.messenger.core.entities.QueryResult;
import reactor.core.publisher.Mono;

public interface NotificationRecordStore<T extends Notification> {

    Mono<T> store(T notification);

    Mono<T> get(String id, String clientId);

    Mono<Void> remove(String id, String clientId);

    Mono<QueryResult<T>> list(String clientId, String templateId, String channelId, int page, int size);
}
