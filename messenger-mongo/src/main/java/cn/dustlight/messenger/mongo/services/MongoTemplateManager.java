package cn.dustlight.messenger.mongo.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;

import static org.springframework.data.mongodb.core.query.Criteria.*;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;
import cn.dustlight.messenger.core.ErrorEnum;
import cn.dustlight.messenger.core.entities.BasicNotificationTemplate;
import cn.dustlight.messenger.core.entities.NotificationTemplate;
import cn.dustlight.messenger.core.entities.QueryResult;
import cn.dustlight.messenger.core.services.NotificationTemplateManager;
import reactor.core.publisher.Mono;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public abstract class MongoTemplateManager<T extends NotificationTemplate> implements NotificationTemplateManager<T> {

    private ReactiveMongoOperations operations;
    private String collectionName;

    @Override
    public Mono<T> getTemplate(String id, String clientId) {
        return operations.findById(id, getEntitiesClass(), collectionName)
                .switchIfEmpty(Mono.error(ErrorEnum.TEMPLATE_NOT_FOUND.getException()))
                .flatMap(template -> clientId.equals(template.getClientId()) ?
                        Mono.just(template) : Mono.error(ErrorEnum.TEMPLATE_NOT_FOUND.getException()));
    }

    @Override
    public Mono<T> createTemplate(T origin) {
        return operations.save(origin,collectionName)
                .onErrorMap(throwable -> ErrorEnum.CREATE_TEMPLATE_FAILED.details(throwable).getException());
    }

    @Override
    public Mono<Void> deleteTemplate(String id, String clientId) {
        return operations.findAndRemove(Query.query(where("_id").is(id).and("clientId").is(clientId)), getEntitiesClass(), collectionName)
                .onErrorMap(throwable -> ErrorEnum.DELETE_TEMPLATE_FAILED.details(throwable).getException())
                .flatMap(t -> {
                    if (t == null)
                        return Mono.error(ErrorEnum.TEMPLATE_NOT_FOUND.getException());
                    return Mono.empty();
                });
    }

    @Override
    public Mono<Void> setTemplate(T template) {
        if (template == null)
            return Mono.error(ErrorEnum.TEMPLATE_NOT_FOUND.getException());
        Update u = new Update();
        if (template.getContent() != null)
            u.set("content", template.getContent());
        if (template.getName() != null)
            u.set("name", template.getName());
        if (template.getStatus() != null)
            u.set("status", template.getStatus());
        u.set("updatedAt", new Date());
        return operations.findAndModify(Query.query(where("_id").is(template.getId()).and("clientId").is(template.getClientId())),
                        u,
                        getEntitiesClass(),
                        collectionName)
                .onErrorMap(throwable -> ErrorEnum.UPDATE_TEMPLATE_FAILED.details(throwable).getException())
                .switchIfEmpty(Mono.error(ErrorEnum.TEMPLATE_NOT_FOUND.getException()))
                .then();
    }

    @Override
    public Mono<QueryResult<T>> getTemplates(String key, int page, int size, String clientId, String owner) {
        Query query = Query.query(where("clientId").is(clientId)
                .and("owner").is(owner));
        if (StringUtils.hasText(key))
            query.addCriteria(TextCriteria.forDefaultLanguage().matching(key));
        query.fields().exclude("content");
        return operations.count(query,
                        getEntitiesClass(),
                        collectionName)
                .flatMap(c -> operations.find(query.with(Pageable.ofSize(size).withPage(page)),
                                getEntitiesClass(),
                                collectionName)
                        .collectList()
                        .map(templates -> new QueryResult<>(c, templates))
                );
    }

    protected abstract Class<T> getEntitiesClass();
}
