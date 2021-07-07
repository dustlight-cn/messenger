package plus.messenger.entities;

import lombok.Getter;
import lombok.Setter;
import plus.messenger.core.entities.MessageContent;

import java.util.HashMap;

@Getter
@Setter
public class DefaultMessageContent implements MessageContent {

    private String templateName;

    private HashMap<String,Object> data;

}
