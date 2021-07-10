package plus.messenger.core.services;

import plus.messenger.core.entities.Notification;
import plus.messenger.core.entities.NotificationTemplate;

public interface NotificationService<T extends Notification, V extends NotificationTemplate> extends Notifier<T, V>, NotificationTemplateManager<V> {


}
