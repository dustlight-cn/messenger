package plus.messenger.core.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class BasicNotificationTemplate implements NotificationTemplate {

    private String id,clientId,owner;
    private String name,content;
    private String status;
    private Map<String,Object> additional;
}
