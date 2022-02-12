package cn.dustlight.messenger.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import cn.dustlight.auth.resources.AuthSecurityWebFilterChainConfiguration;

@SpringBootApplication
public class MessengerServiceApplication extends AuthSecurityWebFilterChainConfiguration {

    public static void main(String[] args) {
        SpringApplication.run(MessengerServiceApplication.class, args);
    }

}
