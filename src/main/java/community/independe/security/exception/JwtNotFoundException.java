package community.independe.security.exception;

public class JwtNotFoundException extends RuntimeException {

    public JwtNotFoundException() {
        super();
    }

    public JwtNotFoundException(String message) {
        super(message);
    }

    public JwtNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtNotFoundException(Throwable cause) {
        super(cause);
    }

    protected JwtNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
