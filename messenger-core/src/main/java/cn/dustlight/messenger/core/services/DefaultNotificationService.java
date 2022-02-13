package cn.dustlight.messenger.core.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import cn.dustlight.messenger.core.entities.Notification;
import cn.dustlight.messenger.core.entities.NotificationTemplate;
import cn.dustlight.messenger.core.entities.QueryResult;
import reactor.core.publisher.Mono;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class DefaultNotificationService<T extends Notification, V extends NotificationTemplate> implements NotificationService<T, V>, NotificationRecordStore<T> {

    private NotificationTemplateManager<V> templateManager;

    private Notifier<T, V> notifier;

    private NotificationRecordStore<T> store;

    @Override
    public Mono<T> sendNotification(T notification, V template) {
        return notifier.sendNotification(notification, template);
    }

    public Mono<T> sendNotification(T notification) {
        notification.setStatus(null);
        notification.setId(null);
        Date time = new Date();
        notification.setCreatedAt(time);
        return templateManager
                .getTemplate(notification.getTemplateId(), notification.getClientId())
                .flatMap(template -> this.sendNotification(notification, template)
                        .flatMap(t -> store.store(t))
                );
    }

    @Override
    public Mono<V> getTemplate(String id, String clientId) {
        return templateManager.getTemplate(id, clientId);
    }

    @Override
    public Mono<V> createTemplate(V origin) {
        return templateManager.createTemplate(origin);
    }

    @Override
    public Mono<Void> deleteTemplate(String id, String clientId) {
        return templateManager.deleteTemplate(id, clientId);
    }

    @Override
    public Mono<Void> setTemplate(V template) {
        return templateManager.setTemplate(template);
    }

    @Override
    public Mono<QueryResult<V>> getTemplates(String id, int page, int size, String clientId, String owner) {
        return templateManager.getTemplates(id, page, size, clientId, owner);
    }

    @Override
    public Mono<T> store(T notification) {
        return store.store(notification);
    }

    @Override
    public Mono<T> get(String id, String clientId) {
        return store.get(id, clientId);
    }

    @Override
    public Mono<Void> remove(String id, String clientId) {
        return store.remove(id, clientId);
    }
}
