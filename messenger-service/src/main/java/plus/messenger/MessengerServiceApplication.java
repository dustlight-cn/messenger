package plus.messenger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import plus.auth.resources.AuthResourceServerProperties;
import plus.auth.resources.AuthSecurityWebFilterChainConfiguration;
import plus.auth.resources.services.AuthJwtAuthenticationConverter;
import plus.auth.resources.services.ReactiveAuthOpaqueTokenIntrospector;
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
