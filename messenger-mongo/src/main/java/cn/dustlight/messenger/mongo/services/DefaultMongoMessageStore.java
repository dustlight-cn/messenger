package cn.dustlight.messenger.mongo.services;

import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import cn.dustlight.messenger.core.entities.BasicMessage;

public class DefaultMongoMessageStore extends MongoMessageStore<BasicMessage> {

    public DefaultMongoMessageStore(ReactiveMongoOperations operations, String collectionName) {
        super(operations, collectionName);
    }

    @Override
    protected Class<BasicMessage> getEntitiesClass() {
        return BasicMessage.class;
    }
}
