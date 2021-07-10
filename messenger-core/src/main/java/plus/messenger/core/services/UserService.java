package plus.messenger.core.services;

import plus.auth.entities.AuthUser;
import plus.auth.entities.AuthUsers;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<AuthUser> getUser(Long uid);

    Mono<AuthUsers> getUsers(Long... uids);

}
