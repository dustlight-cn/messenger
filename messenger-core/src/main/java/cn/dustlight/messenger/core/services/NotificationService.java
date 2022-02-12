package cn.dustlight.messenger.core.services;

import cn.dustlight.messenger.core.entities.Notification;
import cn.dustlight.messenger.core.entities.NotificationTemplate;

public interface NotificationService<T extends Notification, V extends NotificationTemplate> extends Notifier<T, V>, NotificationTemplateManager<V> {


}
