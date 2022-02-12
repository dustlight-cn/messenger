package cn.dustlight.messenger.mongo.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import cn.dustlight.messenger.core.entities.Message;
import cn.dustlight.messenger.core.services.MessageStore;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

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
        if (update.getClientId() != null)
            u.set("clientId", update.getClientId());
        if (update.getReceiver() != null)
            u.set("receiver", update.getReceiver());
        if (update.getContent() != null)
            u.set("content", update.getContent());
        if (update.getCreatedAt() != null)
            u.set("createdAt", update.getCreatedAt());
        if (update.getReadAt() != null)
            u.set("readAt", update.getReadAt());
        if (update.getSender() != null)
            u.set("sender", update.getSender());
        if (update.getSentAt() != null)
            u.set("sentAt", update.getSentAt());
        if (update.getStatus() != null)
            u.set("status", update.getStatus());
        return operations.findAndModify(Query.query(Criteria.where("_id").is(update.getId())), u, getEntitiesClass(), collectionName);
    }

    @Override
    public Flux<T> update(Collection<String> messageIds, T update) {
        Update u = new Update();
        if (update.getClientId() != null)
            u.set("clientId", update.getClientId());
        if (update.getReceiver() != null)
            u.set("receiver", update.getReceiver());
        if (update.getContent() != null)
            u.set("content", update.getContent());
        if (update.getCreatedAt() != null)
            u.set("createdAt", update.getCreatedAt());
        if (update.getReadAt() != null)
            u.set("readAt", update.getReadAt());
        if (update.getSender() != null)
            u.set("sender", update.getSender());
        if (update.getSentAt() != null)
            u.set("sentAt", update.getSentAt());
        if (update.getStatus() != null)
            u.set("status", update.getStatus());

        return operations.updateMulti(Query.query(Criteria.where("_id").in(messageIds)), u, getEntitiesClass(), collectionName)
                .flux()
                .flatMap(updateResult -> operations.find(Query.query(Criteria.where("_id").in(messageIds)), getEntitiesClass(), collectionName));
    }

    @Override
    public Mono<T> getOne(String messageId) {
        return operations.findById(messageId, getEntitiesClass(), collectionName);
    }

    @Override
    public Flux<T> getUnread(String clientId, String receiver) {
        return operations.find(Query.query(Criteria.where("clientId").is(clientId)
                        .and("receiver").is(receiver)
                        .and("sentAt").is(null)),
                getEntitiesClass(), collectionName);
    }

    @Override
    public Flux<T> get(Collection<String> messageIds) {
        return operations.find(Query.query(Criteria.where("_id").in(messageIds)),
                getEntitiesClass(),
                collectionName);
    }
}
