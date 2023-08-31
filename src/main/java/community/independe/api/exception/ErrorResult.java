package community.independe.api.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ErrorResult {

    private HttpStatus status;
    private String message;
    private String exceptionMessage;
}
