package plus.messenger.core.entities;

import java.util.Map;

public interface MessageContent {

    String getTemplateName();

    Map<String,Object> getData();

}
