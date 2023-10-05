package community.independe.api.exception;

import community.independe.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomExceptionAdvice {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResult> customExceptionHandler(CustomException e) {
        ErrorResult errorResult = new ErrorResult(e.getErrorCode().getStatus(), e.getErrorCode().getErrorMessage());
        return new ResponseEntity<>(errorResult, e.getErrorCode().getStatus());
    }
}
