package plus.messenger.application.configurations;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "消息推送服务",
                description = "提供消息推送、消息持久化、消息检索等存储服务。",
                contact = @Contact(
                        name = "Hansin",
                        email = "hansin@goodvoice.com"
                ),
                version = "v1"
        )

)
@SecuritySchemes(value = @SecurityScheme(name = "auth",
        type = SecuritySchemeType.OAUTH2,
        in= SecuritySchemeIn.HEADER,
        scheme = "Bearer",
        flows =  @OAuthFlows(
                implicit = @OAuthFlow(
                        authorizationUrl = "${plus.messenger.authorization-endpoint}")
        )
)
)
public class SpringDocumentConfig {
}
