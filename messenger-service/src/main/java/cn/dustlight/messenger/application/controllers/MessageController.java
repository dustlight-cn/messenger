package cn.dustlight.messenger.application.controllers;

import cn.dustlight.messenger.core.services.MessageStore;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import cn.dustlight.auth.client.reactive.ReactiveAuthClient;
import cn.dustlight.auth.resources.AuthPrincipalUtil;
import cn.dustlight.auth.resources.core.AuthPrincipal;
import cn.dustlight.messenger.core.entities.BasicMessage;
import cn.dustlight.messenger.core.entities.Message;
import cn.dustlight.messenger.core.services.MessageService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@RestController
@RequestMapping("/v1/")
@SecurityRequirement(name = "auth")
@Tag(name = "Messages", description = "消息")
@CrossOrigin
public class MessageController {

    @Autowired
    MessageService service;

    @Autowired
    MessageStore messageStore;

    @PostMapping("messages")
    public Flux<Message> sendMessage(@RequestBody BasicMessage message,
                                     @RequestParam(required = false) String channel,
                                     @RequestParam(name = "cid", required = false) String clientId,
                                     ReactiveAuthClient reactiveAuthClient,
                                     AuthPrincipal principal) {
        return AuthPrincipalUtil.obtainClientId(reactiveAuthClient, clientId, principal)
                .flatMapMany(cid -> {
                    if (StringUtils.hasText(principal.getUidString()))
                        message.setSender(principal.getUidString());
                    message.setClientId(cid);
                    if (StringUtils.hasText(channel))
                        return service.sendMessage(message, channel);
                    else
                        return Flux.from(service.sendMessage(message));
                });
    }

    @GetMapping("chat-list")
    public Flux<Message> getChatList(@RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                     @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                     @RequestParam(name = "cid", required = false) String clientId,
                                     ReactiveAuthClient reactiveAuthClient,
                                     AuthPrincipal principal) {
        return AuthPrincipalUtil.obtainClientId(reactiveAuthClient, clientId, principal)
                .flatMapMany(cid -> messageStore.getChatList(cid, principal.getUidString(), page, size));
    }

    @GetMapping("chat/{target}")
    public Flux<Message> getChat(@PathVariable(name = "target") String target,
                                 @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                 @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                 @RequestParam(name = "cid", required = false) String clientId,
                                 ReactiveAuthClient reactiveAuthClient,
                                 AuthPrincipal principal) {
        return AuthPrincipalUtil.obtainClientId(reactiveAuthClient, clientId, principal)
                .flatMapMany(cid -> messageStore.getChat(cid, principal.getUidString(), target, page, size));
    }

    @PostMapping("messages/read")
    public Mono<Void> markRead(@RequestParam(name = "cid", required = false) String clientId,
                               @RequestBody Collection<String> ids,
                               ReactiveAuthClient reactiveAuthClient,
                               AuthPrincipal principal) {
        return AuthPrincipalUtil.obtainClientId(reactiveAuthClient, clientId, principal)
                .flatMap(cid -> messageStore.markRead(cid, ids, principal.getUidString()));
    }
}
