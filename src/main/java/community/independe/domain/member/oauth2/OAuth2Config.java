package community.independe.domain.member.oauth2;

public class OAuth2Config {

    public enum SocialType {
        NAVER("naver"),
        KAKAO("kakao"),
        ;

        private final String socialName;

        SocialType(String socialName) {
            this.socialName = socialName;
        }

        public String getSocialName() {
            return socialName;
        }
    }
}
