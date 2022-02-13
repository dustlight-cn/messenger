package cn.dustlight.messenger.core.services;

import cn.dustlight.messenger.core.entities.Channel;
import cn.dustlight.messenger.core.entities.QueryResult;
import reactor.core.publisher.Mono;

public interface ChannelService<T extends Channel> {

    Mono<T> getChannel(String channelId,String clientId);

    Mono<T> createChannel(T channel);

    Mono<Void> updateChannel(String channelId, T channel,String user);

    Mono<Void> deleteChannel(String channelId,String clientId);

    Mono<QueryResult<T>> findChannel(String key,
                                     int page,
                                     int size,
                                     String clientId,
                                     String user);
}
