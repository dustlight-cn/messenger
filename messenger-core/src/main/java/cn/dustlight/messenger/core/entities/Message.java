package cn.dustlight.messenger.core.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public interface Message extends Serializable {

    String getId();

    Map<String, Object> getContent();

    String getSender();

    String getReceiver();

    String getClientId();

    Integer getStatus();

    Date getCreatedAt();

    Date getSentAt();

    Date getReadAt();

    void setId(String id);

    void setContent(Map<String, Object> content);

    void setSender(String sender);

    void setReceiver(String receiver);

    void setClientId(String clientId);

    void setStatus(Integer status);

    void setCreatedAt(Date createdAt);

    void setSentAt(Date sentAt);

    void setReadAt(Date readAt);

}
