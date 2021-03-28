package cn.dustlight.messenger.configurations;

import cn.dustlight.messenger.services.DefaultOAuth2TokenService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TokenServiceConfiguration.OAuth2Properties.class)
public class TokenServiceConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "dustlight.messenger.oauth2",
            name = {"client-id", "client-secret", "uri"})
    public DefaultOAuth2TokenService defaultOAuth2TokenService(@Autowired OAuth2Properties properties) {
        return new DefaultOAuth2TokenService(properties.clientId, properties.clientSecret, properties.uri);
    }

    @Getter
    @Setter
    @ConfigurationProperties(prefix = "dustlight.messenger.oauth2")
    public static class OAuth2Properties {

        private String clientId, clientSecret, uri;

    }
}
