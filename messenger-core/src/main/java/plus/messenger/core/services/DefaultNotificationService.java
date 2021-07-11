package plus.messenger.core.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import plus.messenger.core.entities.Notification;
import plus.messenger.core.entities.NotificationTemplate;
import plus.messenger.core.entities.QueryResult;
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
                .getTemplate(notification.getTemplateId())
                .flatMap(template -> this.sendNotification(notification, template)
                        .flatMap(t -> store.store(t))
                );
    }

    @Override
    public Mono<V> getTemplate(String id) {
        return templateManager.getTemplate(id);
    }

    @Override
    public Mono<V> createTemplate(V origin) {
        return templateManager.createTemplate(origin);
    }

    @Override
    public Mono<Void> deleteTemplate(String id) {
        return templateManager.deleteTemplate(id);
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
    public Mono<T> get(String id) {
        return store.get(id);
    }

    @Override
    public Mono<Void> remove(String id) {
        return store.remove(id);
    }
}
