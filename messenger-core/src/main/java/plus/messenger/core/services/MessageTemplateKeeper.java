package plus.messenger.core.services;

import plus.messenger.core.entities.MessageTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 消息模板管理器
 */
public interface MessageTemplateKeeper {

    /**
     * 获取所有模板
     *
     * @return 所有消息模板
     */
    Flux<MessageTemplate> getTemplates();

    /**
     * 获取指定名称的消息模板
     *
     * @param templateName 消息模板名称
     * @return 消息模板
     */
    Mono<MessageTemplate> getTemplate(String templateName);

    /**
     * 储存消息模板
     *
     * @param template 消息模板
     * @return
     */
    Mono<Void> storeTemplate(MessageTemplate template);

}
