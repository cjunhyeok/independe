package community.independe.api.exception;

import community.independe.exception.notfound.CommentNotFountException;
import community.independe.exception.notfound.MemberNotFountException;
import community.independe.exception.notfound.PostNotFountException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class NotFoundExceptionAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MemberNotFountException.class)
    public ErrorResult memberNotFoundHandler(MemberNotFountException e) {
        return new ErrorResult(HttpStatus.BAD_REQUEST, "회원이 존재하지 않습니다.", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PostNotFountException.class)
    public ErrorResult postNotFoundHandler(PostNotFountException e) {
        return new ErrorResult(HttpStatus.BAD_REQUEST, "게시글이 존재하지 않습니다.", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CommentNotFountException.class)
    public ErrorResult commentNotFoundHandler(CommentNotFountException e) {
        return new ErrorResult(HttpStatus.BAD_REQUEST, "댓글이 존재하지 않습니다.", e.getMessage());
    }
}
