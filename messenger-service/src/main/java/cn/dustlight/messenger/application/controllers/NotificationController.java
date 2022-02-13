package cn.dustlight.messenger.application.controllers;

import cn.dustlight.messenger.core.entities.QueryResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import cn.dustlight.auth.client.reactive.ReactiveAuthClient;
import cn.dustlight.auth.resources.AuthPrincipalUtil;
import cn.dustlight.auth.resources.core.AuthPrincipal;
import cn.dustlight.messenger.core.entities.BasicNotification;
import cn.dustlight.messenger.core.entities.Notification;
import cn.dustlight.messenger.core.services.DefaultNotificationService;
import cn.dustlight.messenger.email.EmailNotifier;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/notifications")
@SecurityRequirement(name = "auth")
@Tag(name = "Notifications", description = "通知")
@CrossOrigin
public class NotificationController implements InitializingBean {

    @Autowired
    EmailNotifier emailNotifier;
    @Autowired
    private DefaultNotificationService emailNotificationService;

    private Map<NotificationType, DefaultNotificationService> notifierMap = new HashMap<>();

    protected DefaultNotificationService getManager(NotificationType notificationType) {
        return notifierMap.get(notificationType);
    }

    @Operation(summary = "列出通知列表", description = "")
    @GetMapping("")
    public Mono<QueryResult<Notification>> listNotification(@RequestParam(name = "templateId", required = false) String templateId,
                                                            @RequestParam(name = "channelId", required = false) String channelId,
                                                            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                                            @RequestParam(name = "cid", required = false) String clientId,
                                                            ReactiveAuthClient reactiveAuthClient,
                                                            AuthPrincipal principal) {
        return AuthPrincipalUtil.obtainClientIdRequireMember(reactiveAuthClient, clientId, principal)
                .flatMap(cid -> emailNotificationService.list(cid, templateId, channelId, page, size));
    }

    @Operation(summary = "创建并发送通知", description = "")
    @PostMapping("")
    public Mono<Notification> createNotification(@RequestParam(required = false, name = "type", defaultValue = "EMAIL") NotificationType type,
                                                 @RequestBody BasicNotification notification,
                                                 @RequestParam(name = "cid", required = false) String clientId,
                                                 ReactiveAuthClient reactiveAuthClient,
                                                 AuthPrincipal principal) {
        Mono<String> mono = StringUtils.hasText(principal.getUidString()) ?
                AuthPrincipalUtil.obtainClientIdRequireMember(reactiveAuthClient, clientId, principal)
                : Mono.just(principal.getClientId());
        return mono
                .flatMap(cid -> {
                    notification.setClientId(cid);
                    if (StringUtils.hasText(principal.getUidString()))
                        notification.setSender(principal.getUidString());
                    else
                        notification.setSender(null);
                    return getManager(type).sendNotification(notification);
                });
    }

    @Operation(summary = "根据 ID 获取通知记录", description = "")
    @GetMapping("/{id}")
    public Mono<Notification> getNotification(@PathVariable String id,
                                              @RequestParam(name = "cid", required = false) String clientId,
                                              ReactiveAuthClient reactiveAuthClient,
                                              AuthPrincipal principal) {
        return AuthPrincipalUtil.obtainClientIdRequireMember(reactiveAuthClient, clientId, principal)
                .flatMap(cid -> emailNotificationService.get(id, cid));
    }

    @Operation(summary = "根据 ID 删除通知记录", description = "")
    @DeleteMapping("/{id}")
    public Mono<Void> deleteNotification(@PathVariable String id,
                                         @RequestParam(name = "cid", required = false) String clientId,
                                         ReactiveAuthClient reactiveAuthClient,
                                         AuthPrincipal principal) {
        return AuthPrincipalUtil.obtainClientIdRequireMember(reactiveAuthClient, clientId, principal)
                .flatMap(cid -> emailNotificationService.remove(id, cid));
    }

    @Override
    public void afterPropertiesSet() {
        notifierMap.put(NotificationType.EMAIL, emailNotificationService);
    }

    public enum NotificationType {
        EMAIL
    }
}
