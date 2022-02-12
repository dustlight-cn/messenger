package cn.dustlight.messenger.core.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class BasicMessage implements Message {

    private String id;

    private String sender, receiver, clientId;

    private Map<String, Object> content;

    private Date createdAt, sentAt, readAt;

    private Integer status;

}
