package plus.messenger.core.entities;

import java.util.Collection;
import java.util.Date;

public interface Channel {

    String getId();

    String getClientId();

    String getName();

    String getDescription();

    Collection<String> getOwner();

    Collection<String> getMembers();

    Date getCreatedAt();

    Date getUpdatedAt();
}
