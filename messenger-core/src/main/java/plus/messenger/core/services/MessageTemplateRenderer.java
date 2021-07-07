package plus.messenger.core.services;

import plus.messenger.core.entities.MessageContent;
import plus.messenger.core.entities.MessageTemplate;
import reactor.core.publisher.Mono;

/**
 * 消息
 */
public interface MessageTemplateRenderer {

    /**
     * 渲染消息
     *
     * @param template 消息模板
     * @param content  消息内容
     * @return 渲染完成的消息
     */
    Mono<String> render(MessageTemplate template, MessageContent content);

}
