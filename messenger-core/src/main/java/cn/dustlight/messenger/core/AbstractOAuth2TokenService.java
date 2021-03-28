package cn.dustlight.messenger.core;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public abstract class AbstractOAuth2TokenService<T> implements TokenService {

    private WebClient client;

    private String clientId, clientSecret, uri, base64Header;

    public AbstractOAuth2TokenService(String clientId, String clientSecret, String uri) {
        client = WebClient.create();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.uri = uri;
        refreshBase64Header();
    }

    @Override
    public Mono<MessengerPrincipal> check(String token) {
        return client.method(HttpMethod.POST)
                .uri(uri)
                .header("Authorization", "Basic " + base64Header)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromFormData("token", token))
                .retrieve()
                .bodyToMono(bodyClass())
                .map(this::map)
                .onErrorMap(throwable -> new CheckTokenException("Check token failed: " + throwable.getMessage(), throwable));
    }

    protected abstract Class<T> bodyClass();

    protected abstract MessengerPrincipal map(T body);

    private void refreshBase64Header() {
        base64Header = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
        refreshBase64Header();
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        refreshBase64Header();
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
