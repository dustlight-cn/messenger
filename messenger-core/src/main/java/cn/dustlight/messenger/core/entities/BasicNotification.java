package cn.dustlight.messenger.core.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class BasicNotification implements Notification {

    private String id,templateId,channelId,sender,clientId;

    private Date createdAt,sentAt;

    private String status;

    private Map<String,Object> content;
}
