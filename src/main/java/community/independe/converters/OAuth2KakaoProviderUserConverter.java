package community.independe.converters;

import community.independe.domain.member.oauth2.KakaoUser;
import community.independe.domain.member.oauth2.OAuth2Config;
import community.independe.domain.member.oauth2.ProviderUser;
import community.independe.util.OAuth2Utils;

public class OAuth2KakaoProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser> {
    @Override
    public ProviderUser converter(ProviderUserRequest providerUserRequest) {

        if (!providerUserRequest.clientRegistration().getRegistrationId().equals(OAuth2Config.SocialType.KAKAO.getSocialName())) {
            return null;
        }

        return new KakaoUser(OAuth2Utils.getSubAttributes(providerUserRequest.oAuth2User(), "kakao_account"),
                providerUserRequest.oAuth2User(),
                providerUserRequest.clientRegistration());
    }
}
