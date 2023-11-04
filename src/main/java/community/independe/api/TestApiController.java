package community.independe.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController // json
@RequiredArgsConstructor
public class TestApiController {

    @Operation(description = "CI CD 적용 전")
    @GetMapping("/api/test")
    public ResponseEntity test() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
