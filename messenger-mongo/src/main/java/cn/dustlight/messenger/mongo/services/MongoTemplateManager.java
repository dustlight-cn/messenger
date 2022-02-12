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
    public Mono<T> getTemplate(String id) {
        return operations.findById(id, getEntitiesClass(), collectionName)
                .switchIfEmpty(Mono.error(ErrorEnum.TEMPLATE_NOT_FOUND.getException()));
    }

    @Override
    public Mono<T> createTemplate(T origin) {
        return operations.save(origin,collectionName)
                .onErrorMap(throwable -> ErrorEnum.CREATE_TEMPLATE_FAILED.details(throwable.getMessage()).getException());
    }

    @Override
    public Mono<Void> deleteTemplate(String id) {
        return operations.findAndRemove(Query.query(where("_id").is(id)), getEntitiesClass(), collectionName)
                .onErrorMap(throwable -> ErrorEnum.DELETE_TEMPLATE_FAILED.details(throwable.getMessage()).getException())
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
        BasicNotificationTemplate t = new BasicNotificationTemplate();
        if (template.getContent() != null)
            u.set("content", t.getContent());
        if (template.getContent() != null)
            u.set("name", t.getContent());
        if (template.getContent() != null)
            u.set("content", t.getContent());
        if (template.getContent() != null)
            u.set("status", t.getContent());
        u.set("updatedAt", new Date());
        return operations.findAndModify(Query.query(where("_id").is(template.getId())),
                u,
                getEntitiesClass(),
                collectionName)
                .onErrorMap(throwable -> ErrorEnum.UPDATE_TEMPLATE_FAILED.details(throwable.getMessage()).getException())
                .switchIfEmpty(Mono.error(ErrorEnum.TEMPLATE_NOT_FOUND.getException()))
                .then();
    }

    @Override
    public Mono<QueryResult<T>> getTemplates(String key, int page, int size, String clientId, String owner) {

        return StringUtils.hasText(key) ?
                operations.count(Query.query(TextCriteria.forDefaultLanguage().matching(key))
                                .addCriteria(where("clientId").is(clientId)
                                        .and("owner").is(owner)),
                        getEntitiesClass(),
                        collectionName)
                        .flatMap(c -> operations.find(Query.query(TextCriteria.forDefaultLanguage().matching(key))
                                        .addCriteria(where("clientId").is(clientId)
                                                .and("owner").is(owner))
                                        .with(Pageable.ofSize(size).withPage(page)),
                                getEntitiesClass(),
                                collectionName)
                                .collectList()
                                .map(templates -> new QueryResult<>(c, templates))
                        ) :
                operations.count(Query.query(where("clientId").is(clientId)
                                .and("owner").is(owner)),
                        getEntitiesClass(),
                        collectionName)
                        .flatMap(c -> operations.find(Query.query(where("clientId").is(clientId)
                                        .and("owner").is(owner))
                                        .with(Pageable.ofSize(size).withPage(page)),
                                getEntitiesClass(),
                                collectionName)
                                .collectList()
                                .map(templates -> new QueryResult<>(c, templates))
                        );
    }

    protected abstract Class<T> getEntitiesClass();
}
