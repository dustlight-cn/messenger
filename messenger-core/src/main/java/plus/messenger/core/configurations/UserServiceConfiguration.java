package plus.messenger.core.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import plus.auth.client.reactive.ReactiveAuthClient;
import plus.messenger.core.services.AuthUserService;
import plus.messenger.core.services.UserService;

@Configuration
public class UserServiceConfiguration {

    @Bean
//    @ConditionalOnBean(ReactiveAuthClient.class)
    public UserService authUserService(@Autowired ReactiveAuthClient authClient){
        return new AuthUserService(authClient);
    }
}
