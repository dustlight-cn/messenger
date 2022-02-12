package cn.dustlight.messenger.core.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import cn.dustlight.auth.client.reactive.ReactiveAuthClient;
import cn.dustlight.auth.entities.AuthUser;
import cn.dustlight.auth.entities.AuthUsers;
import reactor.core.publisher.Mono;

@Getter
@Setter
@AllArgsConstructor
public class AuthUserService implements UserService {

    private ReactiveAuthClient reactiveAuthClient;

    @Override
    public Mono<AuthUser> getUser(Long uid) {
        return reactiveAuthClient.getUser(uid);
    }

    @Override
    public Mono<AuthUsers> getUsers(Long... uids) {
        return reactiveAuthClient.getUsers(uids);
    }
}
