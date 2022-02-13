package cn.dustlight.messenger.application.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.dustlight.auth.client.reactive.ReactiveAuthClient;
import cn.dustlight.auth.resources.AuthPrincipalUtil;
import cn.dustlight.auth.resources.core.AuthPrincipal;
import cn.dustlight.messenger.core.entities.BasicNotificationTemplate;
import cn.dustlight.messenger.core.entities.NotificationTemplate;
import cn.dustlight.messenger.core.entities.QueryResult;
import cn.dustlight.messenger.core.services.NotificationTemplateManager;
import cn.dustlight.messenger.core.services.UserService;
import cn.dustlight.messenger.mongo.services.MongoTemplateManager;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Templates", description = "模板")
@RestController
@RequestMapping("/v1/templates")
@SecurityRequirement(name = "auth")
@CrossOrigin
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
                                                  @RequestParam(name = "type",
                                                          required = false,
                                                          defaultValue = "COMMON") TemplateType templateType,
                                                  @RequestParam(name = "cid", required = false) String clientId,
                                                  ReactiveAuthClient reactiveAuthClient,
                                                  AuthPrincipal principal) {
        return AuthPrincipalUtil.obtainClientId(reactiveAuthClient, clientId, principal)
                .flatMap(cid -> getManager(templateType).getTemplate(id, cid));
    }

    @PostMapping("")
    public Mono<NotificationTemplate> createTemplate(@RequestBody BasicNotificationTemplate template,
                                                     @RequestParam(name = "type",
                                                             required = false,
                                                             defaultValue = "COMMON") TemplateType templateType,
                                                     @RequestParam(name = "cid", required = false) String clientId,
                                                     ReactiveAuthClient reactiveAuthClient,
                                                     AuthPrincipal principal) {
        return AuthPrincipalUtil.obtainClientId(reactiveAuthClient, clientId, principal)
                .flatMap(cid -> {
                    template.setClientId(cid);
                    template.setOwner(principal.getUidString());
                    return getManager(templateType).createTemplate(template);
                });
    }

    @PutMapping("/{id}")
    public Mono<Void> updateTemplate(@PathVariable String id,
                                     @RequestBody BasicNotificationTemplate template,
                                     @RequestParam(name = "type",
                                             required = false,
                                             defaultValue = "COMMON") TemplateType templateType,
                                     @RequestParam(name = "cid", required = false) String clientId,
                                     ReactiveAuthClient reactiveAuthClient,
                                     AuthPrincipal principal) {
        return AuthPrincipalUtil.obtainClientId(reactiveAuthClient, clientId, principal)
                .flatMap(cid -> {
                    template.setId(id);
                    template.setClientId(null);
                    template.setOwner(null);
                    return getManager(templateType).setTemplate(template);
                });
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteTemplate(@PathVariable String id,
                                     @RequestParam(name = "type",
                                             required = false,
                                             defaultValue = "COMMON") TemplateType templateType,
                                     @RequestParam(name = "cid", required = false) String clientId,
                                     ReactiveAuthClient reactiveAuthClient,
                                     AuthPrincipal principal) {
        return AuthPrincipalUtil.obtainClientId(reactiveAuthClient, clientId, principal)
                .flatMap(cid -> getManager(templateType).deleteTemplate(id, cid));
    }

    @GetMapping
    public Mono<QueryResult<NotificationTemplate>> findTemplates(@RequestParam(required = false) String key,
                                                                 @RequestParam(required = false, defaultValue = "0") int page,
                                                                 @RequestParam(required = false, defaultValue = "10") int size,
                                                                 @RequestParam(name = "type",
                                                                         required = false,
                                                                         defaultValue = "COMMON") TemplateType templateType,
                                                                 @RequestParam(name = "cid", required = false) String clientId,
                                                                 ReactiveAuthClient reactiveAuthClient,
                                                                 AuthPrincipal principal) {
        return AuthPrincipalUtil.obtainClientId(reactiveAuthClient, clientId, principal)
                .flatMap(cid -> getManager(templateType)
                        .getTemplates(key, page, size, cid, principal.getUidString()));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        managerMap.put(TemplateType.COMMON, mongoTemplateManager);
    }

    public enum TemplateType {
        COMMON
    }
}
