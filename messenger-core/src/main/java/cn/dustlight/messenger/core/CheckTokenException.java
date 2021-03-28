package cn.dustlight.messenger.core;

public class CheckTokenException extends RuntimeException {

    public CheckTokenException(String message) {
        super(message);
    }

    public CheckTokenException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
