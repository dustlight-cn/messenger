package plus.messenger.core.services;

import plus.messenger.core.entities.NotificationTemplate;
import plus.messenger.core.entities.QueryResult;
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
