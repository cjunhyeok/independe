package community.independe.security.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import static org.mockito.Mockito.verify;

public class JwtAccessDeniedHandlerTest {
    @InjectMocks
    private JwtAccessDeniedHandler accessDeniedHandler;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private AccessDeniedException accessDeniedException;

    @Test
    public void testHandle() throws Exception {
        // Mockito 초기화
        MockitoAnnotations.initMocks(this);

        // 테스트 대상 메서드 호출
        accessDeniedHandler.handle(request, response, accessDeniedException);

        // 결과 확인
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}
