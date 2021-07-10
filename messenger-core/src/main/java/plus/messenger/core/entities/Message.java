package plus.messenger.core.entities;

import java.util.Date;
import java.util.Map;

public interface Message {

    String getId();

    Map<String,Object> getContent();

    String getSender();

    String getReceiver();

    String getClientId();

    Integer getStatus();

    Date getCreatedAt();

    Date getSentAt();

    Date getReadAt();
}