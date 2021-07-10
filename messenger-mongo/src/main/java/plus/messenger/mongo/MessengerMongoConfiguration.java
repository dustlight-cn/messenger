package plus.messenger.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import plus.messenger.mongo.services.DefaultMongoChannelService;
import plus.messenger.mongo.services.DefaultMongoTemplateManager;

@EnableConfigurationProperties(MessengerMongoProperties.class)
@Configuration
public class MessengerMongoConfiguration {

    @Bean
    @ConditionalOnBean(ReactiveMongoOperations.class)
    public DefaultMongoChannelService defaultMongoChannelService(@Autowired ReactiveMongoOperations operations,
                                                                 @Autowired MessengerMongoProperties properties){
        return new DefaultMongoChannelService(operations,properties.getChannelCollectionName());
    }

    @Bean
    @ConditionalOnBean(ReactiveMongoOperations.class)
    public DefaultMongoTemplateManager defaultMongoTemplateManager(@Autowired ReactiveMongoOperations operations,
                                                                  @Autowired MessengerMongoProperties properties){
        return new DefaultMongoTemplateManager(operations,properties.getTemplateCollectionName());
    }
}
