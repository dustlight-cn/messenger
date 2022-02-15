package cn.dustlight.messenger.core.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BasicMessage implements Message {

    private String id;

    private String sender, receiver,channel, clientId;

    private Object content;

    private Date createdAt, readAt;

    private Integer status;

}
