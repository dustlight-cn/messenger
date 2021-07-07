package plus.messenger.entities;

import lombok.Getter;
import lombok.Setter;
import plus.messenger.core.entities.MessageTemplate;

@Getter
@Setter
public class DefaultMessageTemplate implements MessageTemplate {

    private String name;

    private String title;

    private String context;

    private boolean ready;

}
