package plus.messenger.entities;

import lombok.Getter;
import lombok.Setter;
import plus.messenger.core.entities.Message;
import plus.messenger.core.entities.MessageChannel;
import plus.messenger.core.entities.MessageContent;
import plus.messenger.core.entities.MessageStatus;

import java.util.Date;

@Getter
@Setter
public class DefaultMessage implements Message {

    private MessageContent content;

    private MessageChannel channel;

    private Date createdAt;

    private Date sentAt;

    private MessageStatus status;

}
