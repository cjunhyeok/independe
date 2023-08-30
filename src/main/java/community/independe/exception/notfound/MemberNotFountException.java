package community.independe.exception.notfound;

public class MemberNotFountException extends RuntimeException {

    public MemberNotFountException() {
        super();
    }

    public MemberNotFountException(String message) {
        super(message);
    }

    public MemberNotFountException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemberNotFountException(Throwable cause) {
        super(cause);
    }

    protected MemberNotFountException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
