package cn.dustlight.messenger.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MessengerClientProperties.class)
public class MessengerClientConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "plus.messenger.client", value = {"client-secret",
            "client-id"})
    public ReactiveMessengerClient reactiveMessengerClient(@Autowired MessengerClientProperties properties,
                                                           @Autowired ObjectMapper objectMapper) {
        return new ReactiveMessengerClient(properties.getClientId(),
                properties.getClientSecret(),
                properties.getTokenUri(),
                objectMapper,
                properties.getApiEndpoint());
    }

}
