package cn.dustlight.messenger.core;

import java.security.Principal;
import java.util.Collection;

public interface MessengerPrincipal extends Principal {

    Collection<String> getAuthority();

    Collection<String> getScope();

    String getClientId();

    Long getUid();

    Object getBody();

}
