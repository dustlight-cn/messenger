package plus.messenger.application.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import plus.auth.resources.AuthPrincipalUtil;
import plus.auth.resources.core.AuthPrincipal;
import plus.messenger.core.entities.BasicNotificationTemplate;
import plus.messenger.core.entities.NotificationTemplate;
import plus.messenger.core.entities.QueryResult;
import plus.messenger.core.services.NotificationTemplateManager;
import plus.messenger.core.services.UserService;
import plus.messenger.mongo.services.MongoTemplateManager;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Templates", description = "模板")
@RestController
@RequestMapping("/v1/templates")
@SecurityRequirement(name = "auth")
public class TemplateController implements InitializingBean {

    @Autowired
    UserService userService;
    @Autowired
    private MongoTemplateManager mongoTemplateManager;

    private Map<TemplateType, NotificationTemplateManager> managerMap = new HashMap<>();

    protected NotificationTemplateManager getManager(TemplateType templateType) {
        return managerMap.get(templateType);
    }

    @GetMapping("/{id}")
    public Mono<NotificationTemplate> getTemplate(@PathVariable String id,
                                                  @RequestParam(name = "type", required = false, defaultValue = "COMMON")
                                                          TemplateType templateType) {
        return getManager(templateType).getTemplate(id);
    }

    @PostMapping("")
    public Mono<NotificationTemplate> createTemplate(@RequestBody BasicNotificationTemplate template,
                                                     AbstractOAuth2TokenAuthenticationToken principal,
                                                     @RequestParam(name = "type", required = false, defaultValue = "COMMON")
                                                             TemplateType templateType) {
        AuthPrincipal authPrincipal = AuthPrincipalUtil.getAuthPrincipal(principal);
        template.setClientId(authPrincipal.getClientId());
        template.setOwner(authPrincipal.getUid().toString());
        return getManager(templateType).createTemplate(template);
    }

    @PutMapping("/{id}")
    public Mono<Void> updateTemplate(@PathVariable String id,
                                     @RequestBody BasicNotificationTemplate template,
                                     AbstractOAuth2TokenAuthenticationToken principal,
                                     @RequestParam(name = "type", required = false, defaultValue = "COMMON")
                                             TemplateType templateType) {

        AuthPrincipal authPrincipal = AuthPrincipalUtil.getAuthPrincipal(principal);
        template.setId(id);
        template.setClientId(null);
        template.setOwner(null);
        return getManager(templateType).setTemplate(template);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteTemplate(@PathVariable String id,
                                     AbstractOAuth2TokenAuthenticationToken principal,
                                     @RequestParam(name = "type", required = false, defaultValue = "COMMON")
                                             TemplateType templateType) {

        AuthPrincipal authPrincipal = AuthPrincipalUtil.getAuthPrincipal(principal);
        return getManager(templateType).deleteTemplate(id);
    }

    @GetMapping
    public Mono<QueryResult<NotificationTemplate>> findTemplates(@RequestParam String key,
                                                                 @RequestParam int page,
                                                                 @RequestParam int size,
                                                                 AbstractOAuth2TokenAuthenticationToken principal,
                                                                 @RequestParam(name = "type", required = false, defaultValue = "COMMON")
                                                                         TemplateType templateType) {
        AuthPrincipal authPrincipal = AuthPrincipalUtil.getAuthPrincipal(principal);
        return getManager(templateType).getTemplates(key, page, size, authPrincipal.getClientId(), authPrincipal.getUid().toString());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        managerMap.put(TemplateType.COMMON, mongoTemplateManager);
    }

    public enum TemplateType {
        COMMON
    }
}
