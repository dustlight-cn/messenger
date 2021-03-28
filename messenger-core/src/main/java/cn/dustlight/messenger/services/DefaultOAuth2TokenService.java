package cn.dustlight.messenger.services;

import cn.dustlight.messenger.core.AbstractOAuth2TokenService;
import cn.dustlight.messenger.core.CheckTokenException;
import cn.dustlight.messenger.core.DefaultMessengerPrincipal;
import cn.dustlight.messenger.core.MessengerPrincipal;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Collection;

public class DefaultOAuth2TokenService extends AbstractOAuth2TokenService<DefaultOAuth2TokenService.DefaultBody> {

    public DefaultOAuth2TokenService(String clientId, String clientSecret, String uri) {
        super(clientId, clientSecret, uri);
    }

    @Override
    protected Class<DefaultBody> bodyClass() {
        return DefaultBody.class;
    }

    @Override
    protected MessengerPrincipal map(DefaultBody body) {
        if (body.exp != null && body.exp.isBefore(Instant.now()))
            throw new CheckTokenException("Token expired!");
        return new DefaultMessengerPrincipal(body.authorities,
                body.scope,
                body.user_name,
                body.client_id,
                Long.valueOf(body.username),
                body);
    }

    @Getter
    @Setter
    public static class DefaultBody {

        private String client_id, username, user_name;
        private Collection<String> authorities, scope;
        private Boolean active;
        private Instant exp;
    }
}
