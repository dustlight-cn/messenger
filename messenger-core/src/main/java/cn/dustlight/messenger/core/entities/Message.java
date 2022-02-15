package cn.dustlight.messenger.core.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public interface Message extends Serializable {

    String getId();

    Object getContent();

    String getSender();

    String getReceiver();

    Channel getChannel();

    String getClientId();

    Integer getStatus();

    Date getCreatedAt();

    Date getReadAt();

    void setId(String id);

    void setContent(Object content);

    void setSender(String sender);

    void setReceiver(String receiver);

    void setChannel(String channel);

    void setClientId(String clientId);

    void setStatus(Integer status);

    void setCreatedAt(Date createdAt);

    void setReadAt(Date readAt);

}
