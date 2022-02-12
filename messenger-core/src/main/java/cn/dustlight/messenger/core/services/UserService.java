package cn.dustlight.messenger.core.services;

import cn.dustlight.auth.entities.AuthUser;
import cn.dustlight.auth.entities.AuthUsers;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<AuthUser> getUser(Long uid);

    Mono<AuthUsers> getUsers(Long... uids);

}
