package plus.messenger.mongo.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import plus.messenger.core.entities.Message;
import plus.messenger.core.services.MessageStore;
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
    public Mono<T> storeOne(T message) {
        return operations.insert(message, collectionName);
    }

    @Override
    public Flux<T> store(Collection<T> messages) {
        return operations.insert(messages, collectionName);
    }

    @Override
    public Mono<T> update(T message) {
        return operations.save(message, collectionName);
    }

    @Override
    public Mono<T> getOne(String messageId) {
        return operations.findById(messageId, getEntitiesClass(), collectionName);
    }

    @Override
    public Flux<T> get(Collection<String> messageIds) {
        return operations.find(Query.query(Criteria.where("_id").in(messageIds)),
                getEntitiesClass(),
                collectionName);
    }
}
