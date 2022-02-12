package cn.dustlight.messenger.core.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import cn.dustlight.auth.entities.AuthUser;
import cn.dustlight.messenger.core.entities.Channel;
import cn.dustlight.messenger.core.entities.Notification;
import cn.dustlight.messenger.core.entities.NotificationTemplate;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

@Getter
@Setter
@AllArgsConstructor
public abstract class AbstractNotifier<T extends Notification, V extends NotificationTemplate> implements Notifier<T, V> {

    private ChannelService channelService;
    private UserService userService;

    protected abstract Mono<Boolean> doSendNotification(T notification, V template, Collection<AuthUser> users) throws Exception;

    @Override
    public Mono<T> sendNotification(T notification, V template) {
        return channelService.getChannel(notification.getChannelId()).flatMap(
                c -> {
                    Channel channel = (Channel) c;
                    HashSet<String> targets = new HashSet<>();
                    if(channel.getMembers() != null)
                        targets.addAll(channel.getMembers());
                    if(channel.getOwner() != null)
                        targets.addAll(channel.getOwner());
                    if(targets.size() == 0)
                        return Mono.just(false);
                    Long[] userIds = new Long[targets.size()];
                    int i = 0;
                    for(String id : targets) {
                        userIds[i++] = Long.valueOf(id);
                    }
                    return userService.getUsers(userIds)
                            .flatMap(authUsers -> {
                                try {
                                    doSendNotification(notification,template,authUsers.getData());
                                    notification.setSentAt(new Date());
                                    return Mono.just(notification);
                                } catch (Exception e){
                                    notification.setStatus(e.getMessage());
                                    return Mono.error(e);
                                }
                            });
                }
        );
    }


}
