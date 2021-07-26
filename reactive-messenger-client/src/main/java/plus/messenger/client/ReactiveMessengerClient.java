package plus.messenger.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.AuthenticatedPrincipalServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;
import plus.messenger.core.ErrorDetails;
import plus.messenger.core.entities.BasicChannel;
import plus.messenger.core.entities.Channel;
import reactor.core.publisher.Mono;

public class ReactiveMessengerClient {

    @Getter
    @Setter
    private String apiEndpoint;

    private WebClient webClient;

    private ObjectMapper mapper;

    private static final String CREATE_CHANNEL_URI = "/v1/channels";
    private static final String DELETE_CHANNEL_URI = "/v1/channels/{id}";

    public ReactiveMessengerClient(ClientRegistration clientRegistration,
                                   ObjectMapper mapper,
                                   String endpoint) {
        this.apiEndpoint = endpoint;
        this.mapper = mapper;
        ReactiveClientRegistrationRepository cr = s -> Mono.just(clientRegistration);
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(cr,
                        new AuthenticatedPrincipalServerOAuth2AuthorizedClientRepository(new InMemoryReactiveOAuth2AuthorizedClientService(cr)));
        oauth.setDefaultClientRegistrationId("auth");
        webClient = WebClient.builder()
                .filter(oauth)
                .build();
    }

    public Mono<Channel> createChannel(BasicChannel channel) {
        return webClient.post()
                .uri(CREATE_CHANNEL_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(toJson(channel))
                .exchangeToMono(clientResponse -> clientResponse.statusCode().is2xxSuccessful() ?
                        clientResponse.bodyToMono(BasicChannel.class) :
                        clientResponse.bodyToMono(ErrorDetails.class).flatMap(errorDetails -> Mono.error(errorDetails.getException())));
    }

    public Mono<Void> deleteChannel(String id) {
        return webClient.delete()
                .uri(DELETE_CHANNEL_URI, id)
                .exchangeToMono(clientResponse -> clientResponse.statusCode().is2xxSuccessful() ?
                        Mono.empty() :
                        clientResponse.bodyToMono(ErrorDetails.class).flatMap(errorDetails -> Mono.error(errorDetails.getException())));
    }

    protected String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("Fail to convert obj to json, cause: %s", e.getMessage()), e);
        }
    }
}
