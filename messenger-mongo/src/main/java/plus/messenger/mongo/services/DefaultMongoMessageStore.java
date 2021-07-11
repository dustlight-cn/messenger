package plus.messenger.mongo.services;

import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import plus.messenger.core.entities.BasicMessage;

public class DefaultMongoMessageStore extends MongoMessageStore<BasicMessage> {

    public DefaultMongoMessageStore(ReactiveMongoOperations operations, String collectionName) {
        super(operations, collectionName);
    }

    @Override
    protected Class<BasicMessage> getEntitiesClass() {
        return BasicMessage.class;
    }
}
