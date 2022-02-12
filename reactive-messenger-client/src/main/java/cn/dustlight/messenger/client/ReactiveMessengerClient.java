package cn.dustlight.messenger.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.*;
import cn.dustlight.messenger.core.ErrorDetails;
import cn.dustlight.messenger.core.entities.BasicChannel;
import cn.dustlight.messenger.core.entities.Channel;
import reactor.core.publisher.Mono;

public class ReactiveMessengerClient {

    @Getter
    @Setter
    private String apiEndpoint;

    private WebClient webClient;

    private ObjectMapper mapper;

    private static final String CREATE_CHANNEL_URI = "/v1/channels";
    private static final String DELETE_CHANNEL_URI = "/v1/channels/{id}";

    public ReactiveMessengerClient(String clientId,
                                   String clientSecret,
                                   String tokenUri,
                                   ObjectMapper mapper,
                                   String endpoint) {
        this.apiEndpoint = endpoint;
        this.mapper = mapper;
        webClient = WebClient.builder()
                .baseUrl(apiEndpoint)
                .filter(new AuthClientFilter(tokenUri, clientId, clientSecret))
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
