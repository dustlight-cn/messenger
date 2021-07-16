package plus.messenger.mongo.services;

import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import plus.messenger.core.entities.BasicNotification;

public class DefaultMongoNotificationStore extends MongoNotificationStore<BasicNotification> {

    public DefaultMongoNotificationStore(ReactiveMongoOperations operations, String collectionName) {
        super(operations, collectionName);
    }

    @Override
    protected Class<BasicNotification> getEntitiesClass() {
        return BasicNotification.class;
    }
}
