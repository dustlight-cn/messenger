package cn.dustlight.messenger.core;

import cn.dustlight.messenger.utils.UriUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.Map;

@Getter
@Setter
public abstract class AbstractWebsocketHandler implements WebSocketHandler, InitializingBean {

    protected final Log logger = LogFactory.getLog(getClass());

    private TokenService tokenService;

    private String tokenParamName = "token";

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        HandshakeInfo handshakeInfo = session.getHandshakeInfo();
        return check(handshakeInfo)
                .flatMap(messengerPrincipal -> map(messengerPrincipal, session))
                .doOnError(throwable -> {
                    logger.error(throwable.getMessage(), throwable);
                    session.close();
                });
    }

    protected Mono<MessengerPrincipal> check(HandshakeInfo handshakeInfo) {
        Map<String, String> params = UriUtils.getParameter(handshakeInfo.getUri());
        String token;
        if (params == null || !params.containsKey(tokenParamName) || (token = params.get(tokenParamName)) == null)
            return Mono.error(new CheckTokenException("Token is null!"));
        return tokenService.check(token);
    }

    protected abstract Mono<Void> map(MessengerPrincipal principal, WebSocketSession session);

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(tokenService, "Token service could not be null!");
        Assert.hasText(tokenParamName, "Token parameter name could not be null or empty!");
    }
}
