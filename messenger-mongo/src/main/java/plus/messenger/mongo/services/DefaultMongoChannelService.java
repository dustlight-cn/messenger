package plus.messenger.mongo.services;

import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import plus.messenger.core.entities.BasicChannel;

public class DefaultMongoChannelService extends MongoChannelService<BasicChannel> {

    public DefaultMongoChannelService(ReactiveMongoOperations operations, String collectionName) {
        super(operations, collectionName);
    }

    @Override
    protected Class<BasicChannel> getEntitiesClass() {
        return BasicChannel.class;
    }

}
