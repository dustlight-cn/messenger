package plus.messenger.core.services;

import plus.messenger.core.entities.Notification;
import plus.messenger.core.entities.NotificationTemplate;
import reactor.core.publisher.Mono;

public interface Notifier<T extends Notification,V extends NotificationTemplate> {

    Mono<T> sendNotification(T notification,V template);

}
