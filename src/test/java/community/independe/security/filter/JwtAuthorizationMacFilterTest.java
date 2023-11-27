package community.independe.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.util.JwtTokenVerifier;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class JwtAuthorizationMacFilterTest {

    @InjectMocks
    private JwtAuthorizationMacFilter jwtAuthorizationMacFilter;
    @Mock
    private JwtTokenVerifier jwtTokenVerifier;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void makeExceptionMessageTest() throws ServletException, IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/posts/new");
        request.addHeader("Authorization", "Bearer token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        CustomException customException = new CustomException(ErrorCode.ACCESS_TOKEN_NOT_FOUND);

        // stub
        doThrow(customException).when(jwtTokenVerifier).verifyToken(request);

        // when
        jwtAuthorizationMacFilter.doFilterInternal(request, response, null);
        Map<String, Object> responseBody = objectMapper.readValue(response.getContentAsString(), Map.class);

        // then
        assertThat(responseBody.get("status")).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
