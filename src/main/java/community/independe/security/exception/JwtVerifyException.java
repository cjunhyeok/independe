package community.independe.security.exception;

public class JwtVerifyException extends RuntimeException {

    public JwtVerifyException() {
        super();
    }

    public JwtVerifyException(String message) {
        super(message);
    }

    public JwtVerifyException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtVerifyException(Throwable cause) {
        super(cause);
    }

    protected JwtVerifyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
