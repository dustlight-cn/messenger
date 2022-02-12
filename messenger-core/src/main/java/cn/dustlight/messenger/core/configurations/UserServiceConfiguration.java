package cn.dustlight.messenger.core.configurations;

import cn.dustlight.messenger.core.services.AuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import cn.dustlight.auth.client.reactive.ReactiveAuthClient;
import cn.dustlight.messenger.core.services.UserService;

@Configuration
public class UserServiceConfiguration {

    @Bean
    @ConditionalOnBean(ReactiveAuthClient.class)
    public UserService authUserService(@Autowired ReactiveAuthClient authClient){
        return new AuthUserService(authClient);
    }
}
