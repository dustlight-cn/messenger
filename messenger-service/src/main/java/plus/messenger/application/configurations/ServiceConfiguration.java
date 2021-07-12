package plus.messenger.application.configurations;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import plus.messenger.application.services.RabbitMqMessageService;
import plus.messenger.core.services.ChannelService;
import plus.messenger.core.services.DefaultNotificationService;
import plus.messenger.core.services.MessageStore;
import plus.messenger.email.EmailNotifier;
import plus.messenger.mongo.services.MongoNotificationStore;
import plus.messenger.mongo.services.MongoTemplateManager;

@Configuration
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
    public Exchange exchange(@Autowired ConnectionFactory factory,
                             @Autowired RabbitAdmin admin) {
        Exchange exchange = new DirectExchange("messenger",
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
}
