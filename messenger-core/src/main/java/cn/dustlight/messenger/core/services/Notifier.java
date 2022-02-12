package cn.dustlight.messenger.core.services;

import cn.dustlight.messenger.core.entities.Notification;
import cn.dustlight.messenger.core.entities.NotificationTemplate;
import reactor.core.publisher.Mono;

public interface Notifier<T extends Notification,V extends NotificationTemplate> {

    Mono<T> sendNotification(T notification,V template);

}
