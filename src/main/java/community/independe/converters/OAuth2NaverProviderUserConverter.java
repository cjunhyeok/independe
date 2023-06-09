package community.independe.converters;

import community.independe.domain.member.oauth2.NaverUser;
import community.independe.domain.member.oauth2.OAuth2Config;
import community.independe.domain.member.oauth2.ProviderUser;
import community.independe.util.OAuth2Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OAuth2NaverProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser> {
    @Override
    public ProviderUser converter(ProviderUserRequest providerUserRequest) {

        if (!providerUserRequest.clientRegistration().getRegistrationId().equals(OAuth2Config.SocialType.NAVER.getSocialName())) {
            return null;
        }

        return new NaverUser(OAuth2Utils.getSubAttributes(providerUserRequest.oAuth2User(), "response"),
                providerUserRequest.oAuth2User(),
                providerUserRequest.clientRegistration()
        );
    }
}
