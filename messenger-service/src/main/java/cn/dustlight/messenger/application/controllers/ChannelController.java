package cn.dustlight.messenger.application.controllers;

import cn.dustlight.messenger.core.entities.QueryResult;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import cn.dustlight.auth.client.reactive.ReactiveAuthClient;
import cn.dustlight.auth.resources.AuthPrincipalUtil;
import cn.dustlight.auth.resources.core.AuthPrincipal;
import cn.dustlight.messenger.core.entities.BasicChannel;
import cn.dustlight.messenger.core.entities.Channel;
import cn.dustlight.messenger.core.services.ChannelService;
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
                                    @RequestParam(name = "cid", required = false) String clientId,
                                    ReactiveAuthClient reactiveAuthClient,
                                    AuthPrincipal principal) {
        return AuthPrincipalUtil.obtainClientId(reactiveAuthClient, clientId, principal)
                .flatMap(cid -> channelService.getChannel(id, cid));
    }

    @PostMapping()
    public Mono<Channel> createChannel(@RequestBody BasicChannel channel,
                                       @RequestParam(name = "cid", required = false) String clientId,
                                       ReactiveAuthClient reactiveAuthClient,
                                       AuthPrincipal principal) {
        return AuthPrincipalUtil.obtainClientId(reactiveAuthClient, clientId, principal)
                .flatMap(cid -> {
                    if (StringUtils.hasText(principal.getUidString()))
                        if (channel.getOwner() != null) {
                            if (!channel.getOwner().contains(principal.getUidString()))
                                channel.getOwner().add(principal.getUidString());
                        } else {
                            channel.setOwner(Arrays.asList(principal.getUidString()));
                        }
                    channel.setId(null);
                    channel.setClientId(cid);
                    Date t = new Date();
                    channel.setCreatedAt(t);
                    channel.setUpdatedAt(t);
                    return channelService.createChannel(channel);
                });
    }

    @DeleteMapping("/{id}")
    public Mono<Channel> deleteChannel(@PathVariable(name = "id") String id,
                                       @RequestParam(name = "cid", required = false) String clientId,
                                       ReactiveAuthClient reactiveAuthClient,
                                       AuthPrincipal principal) {
        return AuthPrincipalUtil.obtainClientId(reactiveAuthClient, clientId, principal)
                .flatMap(cid -> channelService.deleteChannel(id, cid));
    }

    @PutMapping("/{id}")
    public Mono<Channel> updateChannel(@PathVariable(name = "id") String id,
                                       @RequestBody BasicChannel channel,
                                       @RequestParam(name = "cid", required = false) String clientId,
                                       ReactiveAuthClient reactiveAuthClient,
                                       AuthPrincipal principal) {
        return AuthPrincipalUtil.obtainClientId(reactiveAuthClient, clientId, principal)
                .flatMap(cid -> {
                    channel.setClientId(cid);
                    Date t = new Date();
                    channel.setUpdatedAt(t);
                    return channelService.updateChannel(id, channel, principal.getUidString());
                });
    }

    @GetMapping()
    public Mono<QueryResult<Channel>> findChannels(@RequestParam(name = "key", required = false) String key,
                                                   @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                   @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                                   @RequestParam(name = "cid", required = false) String clientId,
                                                   ReactiveAuthClient reactiveAuthClient,
                                                   AuthPrincipal principal) {
        return AuthPrincipalUtil.obtainClientId(reactiveAuthClient, clientId, principal)
                .flatMap(cid -> channelService.findChannel(key, page, size, cid, principal.getUidString()));
    }
}
