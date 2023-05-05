package community.independe.domain.member.oauth2;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class NaverUser extends OAuth2ProviderUser{

    public NaverUser(OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        super((Map<String, Object>) oAuth2User.getAttributes().get("response"), oAuth2User, clientRegistration);
    }

    @Override
    public String getId() { // 식별자
        return (String) getAttributes().get("id");
    }

    @Override
    public String getUsername() { // Id
        return (String) getAttributes().get("email");
    }

    @Override
    public String getNickname() {
        return (String) getAttributes().get("nickname");
    }
}
