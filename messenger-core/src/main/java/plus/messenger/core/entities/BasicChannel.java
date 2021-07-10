package plus.messenger.core.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Date;

@Getter
@Setter
public class BasicChannel implements Channel {

    private String id,clientId;

    private String name,description;

    private Collection<String> owner,members;

    private Date createdAt,updatedAt;

}
