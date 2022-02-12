package cn.dustlight.messenger.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "dustlight.messenger.client")
public class MessengerClientProperties {

    private String tokenUri = "https://api.dustlight.cn/v1/jws",
            apiEndpoint = "https://messenger.dustlight.cn";
    private String clientId, clientSecret;

}
