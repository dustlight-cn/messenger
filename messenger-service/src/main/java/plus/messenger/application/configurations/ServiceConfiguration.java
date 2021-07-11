package plus.messenger.application.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import plus.messenger.core.services.DefaultNotificationService;
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
}
