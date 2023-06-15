package community.independe.domain.member.oauth2;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class NaverUser extends OAuth2ProviderUser{

    public NaverUser(Attributes attributes, OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        super(attributes.getSubAttributes(), oAuth2User, clientRegistration);
    }

    @Override
    public String getId() { // 식별자
        return (String) getAttributes().get("id");
    }

    @Override
    public String getUsername() { // Id
        return (String) getAttributes().get("id");
    }

    @Override
    public String getNickname() {
        return (String) getAttributes().get("nickname");
    }
}
