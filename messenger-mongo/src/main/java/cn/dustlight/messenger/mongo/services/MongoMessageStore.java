package cn.dustlight.messenger.mongo.services;

import cn.dustlight.messenger.core.ErrorEnum;
import cn.dustlight.messenger.core.entities.QueryResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import cn.dustlight.messenger.core.entities.Message;
import cn.dustlight.messenger.core.services.MessageStore;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public abstract class MongoMessageStore<T extends Message> implements MessageStore<T> {

    private ReactiveMongoOperations operations;
    private String collectionName;

    protected abstract Class<T> getEntitiesClass();

    @Override
    public Mono<T> store(T message) {
        return operations.insert(message, collectionName);
    }

    @Override
    public Flux<T> store(Collection<T> messages) {
        return operations.insert(messages, collectionName);
    }

    @Override
    public Mono<T> update(T update) {
        Update u = new Update();
        if (update.getReceiver() != null)
            u.set("receiver", update.getReceiver());
        if (update.getContent() != null)
            u.set("content", update.getContent());
        if (update.getReadAt() != null)
            u.set("readAt", update.getReadAt());
        if (update.getSender() != null)
            u.set("sender", update.getSender());
        if (update.getStatus() != null)
            u.set("status", update.getStatus());
        return operations.findAndModify(Query.query(Criteria.where("_id").is(update.getId())), u, getEntitiesClass(), collectionName);
    }

    @Override
    public Flux<T> update(Collection<String> messageIds, T update, String clientId) {
        Update u = new Update();
        if (update.getReceiver() != null)
            u.set("receiver", update.getReceiver());
        if (update.getContent() != null)
            u.set("content", update.getContent());
        if (update.getReadAt() != null)
            u.set("readAt", update.getReadAt());
        if (update.getSender() != null)
            u.set("sender", update.getSender());
        if (update.getStatus() != null)
            u.set("status", update.getStatus());
        return operations.updateMulti(Query.query(Criteria.where("_id").in(messageIds).and("clientId").is(clientId)), u, getEntitiesClass(), collectionName)
                .flux()
                .flatMap(updateResult -> operations.find(Query.query(Criteria.where("_id").in(messageIds)), getEntitiesClass(), collectionName));
    }

    @Override
    public Mono<T> getOne(String messageId, String clientId) {
        return operations.findOne(Query.query(Criteria.where("_id").is(messageId).and("clientId").is(clientId)), getEntitiesClass(), collectionName);
    }

    @Override
    public Mono<Void> markRead(String clientId, Collection<String> ids, String receiver) {
        Query query = Query.query(Criteria.where("clientId").is(clientId).and("receiver").is(receiver).and("_id").in(ids));
        Update update = new Update();
        update.set("readAt", new Date());
        return operations.updateMulti(query, update, collectionName)
                .onErrorMap(e -> ErrorEnum.UPDATE_RESOURCE_FAILED.details(e).getException())
                .then();
    }

    @Override
    public Flux<T> get(Collection<String> messageIds, String clientId) {
        return operations.find(Query.query(Criteria.where("_id").in(messageIds).and("clientId").is(clientId)),
                getEntitiesClass(),
                collectionName);
    }

    @Override
    public Mono<QueryResult<T>> getChat(String clientId, String user, String target, String offset, int size) {
        Query query = Query.query(Criteria.where("clientId").is(clientId)
                .orOperator(Criteria.where("sender").is(user).and("receiver").is(target),
                        Criteria.where("sender").is(target).and("receiver").is(user)));
        if (StringUtils.hasText(offset))
            query.addCriteria(Criteria.where("_id").lt(new ObjectId(offset)));
        return operations.count(query, collectionName)
                .flatMap(c -> operations.find(query
                                        .with(Pageable.ofSize(size))
                                        .with(Sort.by(Sort.Order.desc("_id"), Sort.Order.desc("createdAt"))),
                                getEntitiesClass(),
                                collectionName)
                        .collectList()
                        .map(msgs -> new QueryResult<>(c, msgs)));
    }

    @Override
    public Flux<T> getChatList(String clientId, String user, String offset, int size) {
        Criteria c = Criteria.where("clientId").is(clientId).and("receiver").is(user);
        Aggregation aggs = StringUtils.hasText(offset) ?
                Aggregation.newAggregation(
                        Aggregation.match(c),
                        Aggregation.sort(Sort.by(Sort.Order.desc("_id"), Sort.Order.desc("createdAt"))),
                        Aggregation.group("$sender").first("$$ROOT").as("doc"),
                        Aggregation.match(Criteria.where("_id").lt(new ObjectId(offset))), // <----------
                        Aggregation.limit(size),
                        Aggregation.replaceRoot("$doc")
                )
                :
                Aggregation.newAggregation(
                        Aggregation.match(c),
                        Aggregation.sort(Sort.by(Sort.Order.desc("_id"), Sort.Order.desc("createdAt"))),
                        Aggregation.group("$sender").first("$$ROOT").as("doc"),
                        Aggregation.limit(size),
                        Aggregation.replaceRoot("$doc")
                );
        return operations.aggregate(aggs, collectionName, getEntitiesClass());
    }
}
