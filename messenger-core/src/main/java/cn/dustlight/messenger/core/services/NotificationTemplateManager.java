package cn.dustlight.messenger.core.services;

import cn.dustlight.messenger.core.entities.NotificationTemplate;
import cn.dustlight.messenger.core.entities.QueryResult;
import reactor.core.publisher.Mono;

public interface NotificationTemplateManager<T extends NotificationTemplate> {

    Mono<T> getTemplate(String id);

    Mono<T> createTemplate(T origin);

    Mono<Void> deleteTemplate(String id);

    Mono<Void> setTemplate(T template);

    Mono<QueryResult<T>> getTemplates(String key,
                                      int page,
                                      int size,
                                      String clientId,
                                      String owner);

}
