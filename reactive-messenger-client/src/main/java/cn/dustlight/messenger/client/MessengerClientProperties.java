package cn.dustlight.messenger.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "plus.messenger.client")
public class MessengerClientProperties {

    private String tokenUri = "http://api.wgv/v1/jws",
            apiEndpoint = "http://api.messenger.wgv";
    private String clientId, clientSecret;

}
