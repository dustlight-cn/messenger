package cn.dustlight.messenger.mongo.services;

import cn.dustlight.messenger.core.entities.QueryResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.Update;
import cn.dustlight.messenger.core.ErrorEnum;
import cn.dustlight.messenger.core.entities.Channel;
import cn.dustlight.messenger.core.services.ChannelService;
import org.springframework.util.StringUtils;
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
    public Mono<T> getChannel(String channelId, String clientId) {
        return operations.findById(channelId, getEntitiesClass(), collectionName)
                .switchIfEmpty(Mono.error(ErrorEnum.CHANNEL_NOT_FOUND.getException()))
                .flatMap(channel -> clientId.equals(channel.getClientId()) ?
                        Mono.just(channel) : Mono.error(ErrorEnum.CHANNEL_NOT_FOUND.getException()));
    }

    @Override
    public Mono<T> createChannel(T channel) {
        return operations.insert(channel,collectionName);
    }

    @Override
    public Mono<Void> updateChannel(String channelId, T channel, String user) {
        Update update = new Update();
        if (channel.getOwner() != null)
            update.set("owner", channel.getOwner());
        if (channel.getMembers() != null)
            update.set("members", channel.getMembers());
        if (channel.getDescription() != null)
            update.set("description", channel.getDescription());
        if (channel.getName() != null)
            update.set("name", channel.getName());
        update.set("updatedAt", new Date());
        return operations.findAndModify(Query.query(where("_id").is(channelId).and("clientId").is(channel.getClientId()).and("owner").in(user)),
                        update,
                        getEntitiesClass(),
                        collectionName)
                .onErrorMap(throwable -> ErrorEnum.UPDATE_CHANNEL_FAILED.details(throwable).getException())
                .switchIfEmpty(Mono.error(ErrorEnum.CHANNEL_NOT_FOUND.getException()))
                .then();
    }

    @Override
    public Mono<Void> deleteChannel(String channelId, String clientId) {
        return operations.findAndRemove(Query.query(where("_id").is(channelId).and("clientId").is(clientId)), getEntitiesClass(), collectionName)
                .onErrorMap(throwable -> ErrorEnum.DELETE_CHANNEL_FAILED.details(throwable).getException())
                .flatMap(t -> {
                    if (t == null)
                        return Mono.error(ErrorEnum.CHANNEL_NOT_FOUND.getException());
                    return Mono.empty();
                });
    }

    @Override
    public Mono<QueryResult<T>> findChannel(String key, int page, int size, String clientId, String user) {
        Query query = Query.query(where("clientId").is(clientId).orOperator(where("owner").in(user),where("members").in(user)));
        if (StringUtils.hasText(key))
            query.addCriteria(TextCriteria.forDefaultLanguage().matching(key));
        return operations.count(query,
                        getEntitiesClass(),
                        collectionName)
                .flatMap(count -> operations.find(query.with(Pageable.ofSize(size).withPage(page)), getEntitiesClass(), collectionName)
                        .collectList()
                        .map(results -> new QueryResult<>(count, results)))
                .onErrorMap(e -> ErrorEnum.CHANNEL_NOT_FOUND.details(e).getException());
    }

    protected abstract Class<T> getEntitiesClass();
}
