package cn.dustlight.messenger.core;

import reactor.core.publisher.Mono;

public interface TokenService {

    Mono<MessengerPrincipal> check(String token) throws CheckTokenException;
}
