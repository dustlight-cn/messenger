package plus.messenger.core.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import plus.auth.client.reactive.ReactiveAuthClient;
import plus.auth.entities.AuthUser;
import plus.auth.entities.AuthUsers;
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
