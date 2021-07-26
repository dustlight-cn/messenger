package plus.messenger.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

@Configuration
@EnableConfigurationProperties(MessengerClientProperties.class)
public class MessengerClientConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "plus.messenger.client", value = {"client-secret",
            "client-id",
            "api-endpoint",
            "token-endpoint"})
    public ReactiveMessengerClient reactiveMessengerClient(@Autowired MessengerClientProperties properties,
                                                           @Autowired ObjectMapper objectMapper) {
        return new ReactiveMessengerClient(ClientRegistration.withRegistrationId("auth")
                .clientId(properties.getClientId())
                .clientSecret(properties.getClientSecret())
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .tokenUri(properties.getTokenUri())
                .build(),
                objectMapper,
                properties.getApiEndpoint());
    }

}
