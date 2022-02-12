package cn.dustlight.messenger.core;

public class MessengerException extends RuntimeException {

    private ErrorDetails errorDetails;

    public MessengerException(String message) {
        super(message);
        this.errorDetails = new ErrorDetails(0,message);
    }

    public MessengerException(String message, Throwable throwable) {
        super(message, throwable);
        this.errorDetails = new ErrorDetails(0,message);
    }

    public MessengerException(ErrorDetails errorDetails) {
        super(errorDetails.getMessage());
        this.errorDetails = errorDetails;
    }

    public MessengerException(ErrorDetails errorDetails, Throwable throwable) {
        super(errorDetails.getMessage(), throwable);
        this.errorDetails = errorDetails;
    }

    public ErrorDetails getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(ErrorDetails errorDetails) {
        this.errorDetails = errorDetails;
    }
}
