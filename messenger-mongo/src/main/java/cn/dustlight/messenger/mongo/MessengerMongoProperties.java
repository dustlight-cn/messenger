package cn.dustlight.messenger.mongo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "dustlight.messenger.mongo")
public class MessengerMongoProperties {

    private String channelCollection = "channel",
            templateCollection = "template",
            notificationCollection = "notification",
            messageCollection = "message";
}
