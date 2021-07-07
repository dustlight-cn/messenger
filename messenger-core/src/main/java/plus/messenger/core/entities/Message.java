package plus.messenger.core.entities;

import plus.auth.entities.AuthUser;

import java.util.Date;

/**
 * 消息
 */
public interface Message {

    AuthUser getReceiver();

    AuthUser getSender();

    String getContent();

    String getTitle();

    Date getCreatedAt();

    Date getSentAt();

    MessageStatus getStatus();
}
