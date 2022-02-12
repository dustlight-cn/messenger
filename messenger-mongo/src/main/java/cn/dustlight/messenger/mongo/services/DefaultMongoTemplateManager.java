package cn.dustlight.messenger.mongo.services;

import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import cn.dustlight.messenger.core.entities.BasicNotificationTemplate;
import reactor.core.publisher.Mono;

public class DefaultMongoTemplateManager extends MongoTemplateManager<BasicNotificationTemplate> {

    public DefaultMongoTemplateManager(ReactiveMongoOperations operations, String collectionName) {
        super(operations, collectionName);
    }

    @Override
    public Mono<BasicNotificationTemplate> createTemplate(BasicNotificationTemplate origin) {
        origin.setId(null);
        return super.createTemplate(origin);
    }

    @Override
    protected Class<BasicNotificationTemplate> getEntitiesClass() {
        return BasicNotificationTemplate.class;
    }
}
