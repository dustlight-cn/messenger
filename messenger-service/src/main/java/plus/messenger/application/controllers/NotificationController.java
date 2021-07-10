package plus.messenger.application.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.web.bind.annotation.*;
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
            @RequestParam(required = false,name = "type",defaultValue = "EMAIL") NotificationType type,
            @RequestBody BasicNotification notification,
            AbstractOAuth2TokenAuthenticationToken principal) {
        return getManager(type).sendNotification(notification);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        notifierMap.put(NotificationType.EMAIL,emailNotificationService);
    }

    public enum NotificationType {
        EMAIL
    }
}
