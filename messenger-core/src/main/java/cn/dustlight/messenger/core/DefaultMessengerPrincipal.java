package cn.dustlight.messenger.core;

import lombok.*;

import java.util.Collection;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DefaultMessengerPrincipal implements MessengerPrincipal {

    private Collection<String> authority, scope;
    private String name, clientId;
    private Long uid;
    private Object body;

}
