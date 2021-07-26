package plus.messenger.application.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import plus.auth.resources.AuthPrincipalUtil;
import plus.auth.resources.core.AuthPrincipal;
import plus.messenger.core.entities.BasicMessage;
import plus.messenger.core.entities.Message;
import plus.messenger.core.services.MessageService;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/v1/messages")
@SecurityRequirement(name = "auth")
@Tag(name = "Messages", description = "消息")
@CrossOrigin
public class MessageController {

    @Autowired
    MessageService service;

    @PostMapping()
    public Flux<Message> sendMessage(@RequestBody BasicMessage message,
                                     @RequestParam(required = false) String channel,
                                     AbstractOAuth2TokenAuthenticationToken principal) {
        AuthPrincipal authPrincipal = AuthPrincipalUtil.getAuthPrincipal(principal);
        if (authPrincipal.getUid() != null)
            message.setSender(authPrincipal.getUidString());
        message.setClientId(authPrincipal.getClientId());
        if (StringUtils.hasText(channel))
            return service.sendMessage(message, channel);
        else
            return Flux.from(service.sendMessage(message));
    }

}
