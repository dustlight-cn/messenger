package plus.messenger.application.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import plus.auth.resources.AuthPrincipalUtil;
import plus.auth.resources.core.AuthPrincipal;
import plus.messenger.core.entities.BasicChannel;
import plus.messenger.core.entities.Channel;
import plus.messenger.core.services.ChannelService;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Date;

@Tag(name = "Channels", description = "频道")
@RestController
@RequestMapping("/v1/channels")
@SecurityRequirement(name = "auth")
@CrossOrigin
public class ChannelController {

    @Autowired
    private ChannelService channelService;

    @GetMapping("/{id}")
    public Mono<Channel> getChannel(@PathVariable(name = "id") String id,
                                    AbstractOAuth2TokenAuthenticationToken principal) {
        AuthPrincipal authPrincipal = AuthPrincipalUtil.getAuthPrincipal(principal);
        return channelService.getChannel(id);
    }

    @PostMapping()
    public Mono<Channel> createChannel(@RequestBody BasicChannel channel,
                                       AbstractOAuth2TokenAuthenticationToken principal) {
        AuthPrincipal authPrincipal = AuthPrincipalUtil.getAuthPrincipal(principal);
        if (channel.getOwner() != null) {
            if (!channel.getOwner().contains(authPrincipal.getUidString()))
                channel.getOwner().add(authPrincipal.getUidString());
        } else {
            channel.setOwner(Arrays.asList(authPrincipal.getUidString()));
        }
        channel.setId(null);
        channel.setClientId(authPrincipal.getClientId());

        Date t = new Date();
        channel.setCreatedAt(t);
        channel.setUpdatedAt(t);
        return channelService.createChannel(channel);
    }

    @DeleteMapping("/{id}")
    public Mono<Channel> deleteChannel(@PathVariable(name = "id") String id,
                                       AbstractOAuth2TokenAuthenticationToken principal) {
        AuthPrincipal authPrincipal = AuthPrincipalUtil.getAuthPrincipal(principal);
        return channelService.deleteChannel(id);
    }

    @PutMapping("/{id}")
    public Mono<Channel> updateChannel(@PathVariable(name = "id") String id,
                                       @RequestBody BasicChannel channel,
                                       AbstractOAuth2TokenAuthenticationToken principal) {
        AuthPrincipal authPrincipal = AuthPrincipalUtil.getAuthPrincipal(principal);
        return channelService.updateChannel(id, channel);
    }
}
