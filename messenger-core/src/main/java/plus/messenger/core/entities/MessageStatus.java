package plus.messenger.core.entities;

/**
 * 消息状态
 */
public enum MessageStatus {

    /**
     * 排队中
     */
    QUEUING,
    /**
     * 已送达
     */
    DELIVERED,
    /**
     * 重新发送
     */
    REQUEUE,
    /**
     * 发送失败
     */
    FAILED
}
