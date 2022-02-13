package cn.dustlight.messenger.mongo.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import cn.dustlight.messenger.core.ErrorEnum;
import cn.dustlight.messenger.core.entities.Notification;
import cn.dustlight.messenger.core.services.NotificationRecordStore;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Getter
@Setter
@AllArgsConstructor
public abstract class MongoNotificationStore<T extends Notification> implements NotificationRecordStore<T> {

    private ReactiveMongoOperations operations;
    private String collectionName;

    @Override
    public Mono<T> store(T notification) {
        return operations.save(notification, collectionName);
    }

    @Override
    public Mono<T> get(String id) {
        return operations.findById(id, getEntitiesClass(), collectionName);
    }

    @Override
    public Mono<Void> remove(String id) {
        return operations.findAndRemove(Query.query(where("_id").is(id)),getEntitiesClass(),collectionName)
                .flatMap(n -> {
                    if(n == null)
                        return Mono.error(ErrorEnum.DELETE_RESOURCE_FAILED.getException());
                    return Mono.empty();
                });
    }

    protected abstract Class<T> getEntitiesClass();
}
