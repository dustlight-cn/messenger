package plus.messenger.core.entities;

import java.util.Date;
import java.util.Map;

public interface Notification {

    String getId();

    String getTemplateId();

    Map<String,Object> getContent();

    String getChannelId();

    String getSender();

    String getClientId();

    Date getCreatedAt();

    Date getSentAt();

    String getStatus();

    void setId(String id);

    void setTemplateId(String templateId);

    void setContent(Map<String,Object> content);

    void setChannelId(String channelId);

    void setSender(String sender);

    void setClientId(String clientId);

    void setCreatedAt(Date createdAt);

    void setSentAt(Date createdAt);

    void setStatus(String status);
}
