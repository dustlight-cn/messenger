package cn.dustlight.messenger.mongo;

import cn.dustlight.messenger.mongo.services.DefaultMongoMessageStore;
import cn.dustlight.messenger.mongo.services.DefaultMongoNotificationStore;
import cn.dustlight.messenger.mongo.services.DefaultMongoTemplateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import cn.dustlight.messenger.mongo.services.DefaultMongoChannelService;

@EnableConfigurationProperties(MessengerMongoProperties.class)
@Configuration
public class MessengerMongoConfiguration {

    @Bean
    @ConditionalOnBean(ReactiveMongoOperations.class)
    public DefaultMongoChannelService defaultMongoChannelService(@Autowired ReactiveMongoOperations operations,
                                                                 @Autowired MessengerMongoProperties properties){
        return new DefaultMongoChannelService(operations,properties.getChannelCollection());
    }

    @Bean
    @ConditionalOnBean(ReactiveMongoOperations.class)
    public DefaultMongoTemplateManager defaultMongoTemplateManager(@Autowired ReactiveMongoOperations operations,
                                                                   @Autowired MessengerMongoProperties properties){
        return new DefaultMongoTemplateManager(operations,properties.getTemplateCollection());
    }

    @Bean
    @ConditionalOnBean(ReactiveMongoOperations.class)
    public DefaultMongoNotificationStore defaultMongoNotificationStore(@Autowired ReactiveMongoOperations operations,
                                                                       @Autowired MessengerMongoProperties properties){
        return new DefaultMongoNotificationStore(operations,properties.getNotificationCollection());
    }

    @Bean
    @ConditionalOnBean(ReactiveMongoOperations.class)
    public DefaultMongoMessageStore defaultMongoMessageStore(@Autowired ReactiveMongoOperations operations,
                                                             @Autowired MessengerMongoProperties properties){
        return new DefaultMongoMessageStore(operations,properties.getMessageCollection());
    }
}
