package community.independe.exception.notfound;

public class CommentNotFountException extends RuntimeException {

    public CommentNotFountException() {
        super();
    }

    public CommentNotFountException(String message) {
        super(message);
    }

    public CommentNotFountException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommentNotFountException(Throwable cause) {
        super(cause);
    }

    protected CommentNotFountException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
