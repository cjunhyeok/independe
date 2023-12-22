package community.independe.security.exception;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JwtAuthenticationEntryPointTest {

    @Test
    void commenceTest() throws ServletException, IOException {
        // given
        JwtAuthenticationEntryPoint entryPoint = new JwtAuthenticationEntryPoint();

        // Mock 객체 생성
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException authException = new AuthenticationException("Unauthorized") {};

        // 테스트 대상 메서드 호출
        entryPoint.commence(request, response, authException);

        // 결과 확인
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }
}
