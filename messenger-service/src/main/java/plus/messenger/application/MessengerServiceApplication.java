package plus.messenger.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import plus.auth.resources.AuthSecurityWebFilterChainConfiguration;
import reactor.core.publisher.Mono;

import java.security.Principal;

@SpringBootApplication
@RestController
public class MessengerServiceApplication extends AuthSecurityWebFilterChainConfiguration {


    public static void main(String[] args) {
        SpringApplication.run(MessengerServiceApplication.class, args);
    }


    @GetMapping("/test")
    public Mono<Object> test(Principal authentication){
        return Mono.just(authentication.getName());
    }

    @Override
    protected ServerHttpSecurity configure(ServerHttpSecurity http) {
        return super.configure(http);
    }
}
