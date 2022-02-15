package cn.dustlight.messenger.application.services;

import cn.dustlight.auth.client.reactive.ReactiveAuthClient;
import cn.dustlight.auth.resources.AuthPrincipalUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import cn.dustlight.auth.resources.core.AuthPrincipal;
import cn.dustlight.auth.resources.core.CheckTokenException;
import cn.dustlight.auth.resources.core.TokenService;
import cn.dustlight.messenger.core.utils.UriUtils;
import reactor.core.publisher.Mono;

import java.util.Map;

@Getter
@Setter
public abstract class AbstractWebsocketHandler implements WebSocketHandler, InitializingBean {

    protected final Log logger = LogFactory.getLog(getClass());

    private TokenService tokenService;
    private ReactiveAuthClient authClient;

    private String tokenParamName = "token";
    private String clientIdParamName = "cid";

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        HandshakeInfo handshakeInfo = session.getHandshakeInfo();
        return check(handshakeInfo)
                .flatMap(principalAndClientId -> map(principalAndClientId.principal, principalAndClientId.clientId, session))
                .doOnError(throwable -> {
                    logger.error(throwable.getMessage(), throwable);
                    session.close();
                });
    }

    protected Mono<AuthPrincipalAndClientId> check(HandshakeInfo handshakeInfo) {
        Map<String, String> params = UriUtils.getParameter(handshakeInfo.getUri());
        String token;
        if (params == null || !params.containsKey(tokenParamName) || (token = params.get(tokenParamName)) == null)
            return Mono.error(new CheckTokenException("Token is null!"));
        return tokenService.check(token)
                .flatMap(authPrincipal -> AuthPrincipalUtil.obtainClientId(authClient, params.get(clientIdParamName), authPrincipal)
                        .map(cid -> new AuthPrincipalAndClientId(authPrincipal, cid)));
    }

    protected abstract Mono<Void> map(AuthPrincipal principal, String clientId, WebSocketSession session);

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(tokenService, "Token service could not be null!");
        Assert.hasText(tokenParamName, "Token parameter name could not be null or empty!");
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class AuthPrincipalAndClientId {

        private AuthPrincipal principal;
        private String clientId;

    }
}
