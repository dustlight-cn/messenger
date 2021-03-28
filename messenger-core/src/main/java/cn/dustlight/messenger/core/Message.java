package cn.dustlight.messenger.core;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Message implements Serializable {

    private String uid;
    private Object data;

    @Override
    public String toString() {
        return "Message{" +
                "uid='" + uid + '\'' +
                ", data=" + data +
                '}';
    }
}
