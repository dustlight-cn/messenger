package cn.dustlight.messenger.application.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import cn.dustlight.auth.resources.AuthSecurityWebFilterChainConfiguration;

@EnableReactiveMethodSecurity
@Configuration
public class SecurityConfig extends AuthSecurityWebFilterChainConfiguration {

    @Override
    protected ServerHttpSecurity configure(ServerHttpSecurity http) {
        return http.authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .pathMatchers("/v1/connection").permitAll()
                .pathMatchers("/v1/**").authenticated()
                .anyExchange().permitAll()
                .and();
    }
}
