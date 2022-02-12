package cn.dustlight.messenger.client;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

import java.time.Instant;

public class AuthClientFilter implements ExchangeFilterFunction {

    private volatile Mono<OAuth2AccessToken> accessToken;
    private static final ReactiveOAuth2AuthorizedClientProvider DEFAULT_AUTHORIZED_CLIENT_PROVIDER = ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
            .authorizationCode()
            .refreshToken()
            .clientCredentials()
            .password()
            .build();
    private String tokenUri, clientId, clientSecret;

    public AuthClientFilter(String tokenUri, String clientId, String clientSecret) {
        this.tokenUri = tokenUri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        return getAccessToken()
                .flatMap(oAuth2AccessToken -> next.exchange(ClientRequest.from(request)
                        .header("Authorization", String.format("Bearer %s", oAuth2AccessToken.getTokenValue()))
                        .build()));
    }

    protected Mono<OAuth2AccessToken> getAccessToken() {
        if (accessToken == null) {
            return fetchAccessToken();
        } else {
            return accessToken.flatMap(oAuth2AccessToken -> {
                if (Instant.now().isAfter(oAuth2AccessToken.getExpiresAt()))
                    return fetchAccessToken();
                else
                    return accessToken;
            });
        }
    }

    protected Mono<OAuth2AccessToken> fetchAccessToken() {
        synchronized (AuthClientFilter.class) {
            ClientRegistration cr = ClientRegistration.withRegistrationId("auth")
                    .tokenUri(tokenUri)
                    .clientSecret(clientSecret)
                    .clientId(clientId)
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .build();

            return (accessToken = DEFAULT_AUTHORIZED_CLIENT_PROVIDER
                    .authorize(OAuth2AuthorizationContext
                            .withClientRegistration(cr)
                            .principal(new UsernamePasswordAuthenticationToken(clientId, clientSecret))
                            .build())
                    .map(oAuth2AuthorizedClient -> oAuth2AuthorizedClient.getAccessToken()));
        }
    }
}
