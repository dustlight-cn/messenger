package plus.messenger.core.entities;

/**
 * 消息模板
 */
public interface MessageTemplate {

    /**
     * 获取消息模板名称
     *
     * @return 模板名称
     */
    String getName();

    /**
     * 获取模板是否就绪
     *
     * @return 模板是否就绪
     */
    Boolean isReady();

    /**
     * 获取模板标题
     *
     * @return 模板标题
     */
    String getTitle();

    /**
     * 获取模板内容
     *
     * @return 模板内容
     */
    String getContext();
}
