package plus.messenger.application.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import plus.auth.client.reactive.ReactiveAuthClient;
import plus.auth.resources.AuthPrincipalUtil;
import plus.auth.resources.core.AuthPrincipal;
import plus.messenger.core.entities.BasicNotification;
import plus.messenger.core.entities.Notification;
import plus.messenger.core.services.DefaultNotificationService;
import plus.messenger.email.EmailNotifier;
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

    @PostMapping("")
    public Mono<Notification> createNotification(
            @RequestParam(required = false, name = "type", defaultValue = "EMAIL") NotificationType type,
            @RequestBody BasicNotification notification,
            @RequestParam(name = "cid", required = false) String clientId,
            ReactiveAuthClient reactiveAuthClient,
            AuthPrincipal principal) {
        return AuthPrincipalUtil.obtainClientId(reactiveAuthClient, clientId, principal)
                .flatMap(cid -> {
                    notification.setClientId(cid);
                    if (StringUtils.hasText(principal.getUidString()))
                        notification.setSender(principal.getUidString());
                    return getManager(type).sendNotification(notification);
                });
    }

    @GetMapping("/{id}")
    public Mono<Notification> getNotification(@PathVariable String id,
                                              @RequestParam(name = "cid", required = false) String clientId,
                                              ReactiveAuthClient reactiveAuthClient,
                                              AuthPrincipal principal) {
        return AuthPrincipalUtil.obtainClientId(reactiveAuthClient, clientId, principal)
                .flatMap(cid -> emailNotificationService.get(id));
    }

    @Override
    public void afterPropertiesSet() {
        notifierMap.put(NotificationType.EMAIL, emailNotificationService);
    }

    public enum NotificationType {
        EMAIL
    }
}
