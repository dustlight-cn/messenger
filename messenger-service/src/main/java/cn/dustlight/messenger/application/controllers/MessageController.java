package cn.dustlight.messenger.application.controllers;

import cn.dustlight.messenger.core.entities.QueryResult;
import cn.dustlight.messenger.core.services.MessageStore;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "创建并发送消息", description = "如果提供了 channel 则发送给频道内所有人")
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
                    else
                        message.setSender(null);
                    message.setClientId(cid);
                    if (StringUtils.hasText(channel))
                        return service.sendMessage(message, channel);
                    else
                        return Flux.from(service.sendMessage(message));
                });
    }

    @Operation(summary = "获取最新消息列表", description = "以发信者 ID 分组的最新消息列表")
    @GetMapping("chat-list")
    public Flux<Message> getChatList(@RequestParam(name = "offset", required = false) String offset,
                                     @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                     @RequestParam(name = "cid", required = false) String clientId,
                                     ReactiveAuthClient reactiveAuthClient,
                                     AuthPrincipal principal) {
        return AuthPrincipalUtil.obtainClientId(reactiveAuthClient, clientId, principal)
                .flatMapMany(cid -> messageStore.getChatList(cid, principal.getUidString(), offset, size));
    }

    @Operation(summary = "获取消息列表", description = "获取与目标的对话")
    @GetMapping("chat/{target}")
    public Mono<QueryResult<Message>> getChat(@PathVariable(name = "target") String target,
                                     @RequestParam(name = "offset", required = false) String offset,
                                     @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                     @RequestParam(name = "cid", required = false) String clientId,
                                     ReactiveAuthClient reactiveAuthClient,
                                     AuthPrincipal principal) {
        return AuthPrincipalUtil.obtainClientId(reactiveAuthClient, clientId, principal)
                .flatMap(cid -> messageStore.getChat(cid, principal.getUidString(), target, offset, size));
    }

    @Operation(summary = "标记消息为已读", description = "")
    @PostMapping("messages/read")
    public Mono<Void> markRead(@RequestParam(name = "cid", required = false) String clientId,
                               @RequestBody Collection<String> ids,
                               ReactiveAuthClient reactiveAuthClient,
                               AuthPrincipal principal) {
        return AuthPrincipalUtil.obtainClientId(reactiveAuthClient, clientId, principal)
                .flatMap(cid -> messageStore.markRead(cid, ids, principal.getUidString()));
    }
}
