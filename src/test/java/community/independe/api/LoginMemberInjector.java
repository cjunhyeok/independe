package community.independe.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.independe.service.MemberService;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class LoginMemberInjector {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberService memberService;
    private ObjectMapper objectMapper = new ObjectMapper();
    private String refreshToken;
    private String accessToken;

    public void makeAccessAndRefreshToken() throws Exception {
        memberService.join("testUsername", "testPasswrod1!", "testNickname", null, null);

        String username = "testUsername";
        String password = "testPasswrod1!";

        ResultActions perform = mockMvc.perform(post("/api/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "username", username,
                        "password", password
                )))
                .with(csrf()));

        Cookie refreshTokenCookie = perform.andReturn().getResponse().getCookie("refreshToken");
        refreshToken = refreshTokenCookie.getValue();
        accessToken = perform.andReturn().getResponse().getHeader("Authorization");
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
