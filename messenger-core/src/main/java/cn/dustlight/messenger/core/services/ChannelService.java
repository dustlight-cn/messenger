package cn.dustlight.messenger.core.services;

import cn.dustlight.messenger.core.entities.Channel;
import reactor.core.publisher.Mono;

public interface ChannelService<T extends Channel> {

    Mono<T> getChannel(String channelId);

    Mono<T> createChannel(T channel);

    Mono<Void> updateChannel(String channelId,T channel);

    Mono<Void> deleteChannel(String channelId);
}