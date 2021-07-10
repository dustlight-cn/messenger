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
}
