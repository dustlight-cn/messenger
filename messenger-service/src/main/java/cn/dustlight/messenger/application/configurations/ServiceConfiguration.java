package cn.dustlight.messenger.application.configurations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import cn.dustlight.messenger.application.services.RabbitMqMessageService;
import cn.dustlight.messenger.core.services.ChannelService;
import cn.dustlight.messenger.core.services.DefaultNotificationService;
import cn.dustlight.messenger.core.services.MessageStore;
import cn.dustlight.messenger.email.EmailNotifier;
import cn.dustlight.messenger.mongo.services.MongoNotificationStore;
import cn.dustlight.messenger.mongo.services.MongoTemplateManager;

@Configuration
@EnableConfigurationProperties(ServiceConfiguration.RabbitProperties.class)
public class ServiceConfiguration {

    @Bean
    public DefaultNotificationService emailNotificationService(@Autowired EmailNotifier emailNotifier,
                                                               @Autowired MongoTemplateManager mongoTemplateManager,
                                                               @Autowired MongoNotificationStore mongoNotificationStore) {
        return new DefaultNotificationService(mongoTemplateManager, emailNotifier, mongoNotificationStore);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(@Autowired ConnectionFactory factory) {
        return new RabbitAdmin(factory);
    }

    @Bean
    public Exchange exchange(@Autowired RabbitProperties properties,
                             @Autowired RabbitAdmin admin) {
        Exchange exchange = new DirectExchange(properties.getExchange(),
                true,
                false);
        admin.declareExchange(exchange);
        return exchange;
    }

    @Bean
    public RabbitTemplate template(@Autowired ConnectionFactory factory,
                                   @Autowired Exchange exchange) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setExchange(exchange.getName());
        return template;
    }

    @Bean
    public RabbitMqMessageService rabbitMqMessageService(@Autowired RabbitTemplate template,
                                                         @Autowired MessageStore messageStore,
                                                         @Autowired ChannelService channelService,
                                                         @Autowired RabbitAdmin rabbitAdmin) {
        return new RabbitMqMessageService(messageStore, channelService, template, rabbitAdmin);
    }

    @Getter
    @Setter
    @ConfigurationProperties(prefix = "dustlight.messenger.rabbit")
    public static class RabbitProperties {
        private String exchange = "messenger";
    }
}
