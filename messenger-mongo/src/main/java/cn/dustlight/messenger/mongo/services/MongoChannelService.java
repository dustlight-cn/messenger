package cn.dustlight.messenger.mongo.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import cn.dustlight.messenger.core.ErrorEnum;
import cn.dustlight.messenger.core.entities.Channel;
import cn.dustlight.messenger.core.services.ChannelService;
import reactor.core.publisher.Mono;

import java.util.Date;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Getter
@Setter
@AllArgsConstructor
public abstract class MongoChannelService<T extends Channel> implements ChannelService<T> {

    private ReactiveMongoOperations operations;
    private String collectionName;

    @Override
    public Mono<T> getChannel(String channelId) {
        return operations.findById(channelId,getEntitiesClass(),collectionName)
                .switchIfEmpty(Mono.error(ErrorEnum.UNKNOWN.getException()));
    }

    @Override
    public Mono<T> createChannel(T channel) {
        return operations.insert(channel,collectionName);
    }

    @Override
    public Mono<Void> updateChannel(String channelId, T channel) {
        Update update = new Update();
        if(channel.getOwner() != null)
            update.set("owner", channel.getOwner());
        if(channel.getMembers() != null)
            update.set("member", channel.getMembers());
        if(channel.getDescription() != null)
            update.set("description", channel.getDescription());
        if(channel.getName() != null)
            update.set("name", channel.getName());
        update.set("updatedAt",new Date());
        return operations.findAndModify(Query.query(where("_id").is(channelId)),
                update,
                getEntitiesClass(),
                collectionName)
                .onErrorMap(throwable -> ErrorEnum.UPDATE_CHANNEL_FAILED.details(throwable.getMessage()).getException())
                .switchIfEmpty(Mono.error(ErrorEnum.CHANNEL_NOT_FOUND.getException()))
                .then();
    }

    @Override
    public Mono<Void> deleteChannel(String channelId) {
        return operations.findAndRemove(Query.query(where("_id").is(channelId)), getEntitiesClass(), collectionName)
                .onErrorMap(throwable -> ErrorEnum.DELETE_CHANNEL_FAILED.details(throwable.getMessage()).getException())
                .flatMap(t -> {
                    if (t == null)
                        return Mono.error(ErrorEnum.CHANNEL_NOT_FOUND.getException());
                    return Mono.empty();
                });
    }

    protected abstract Class<T> getEntitiesClass();
}
