package community.independe.util;

public abstract class UrlList {

    private static final String[] whiteList = {"/",
            "/actuator/**",
            "/ws",
            "/ws/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/oauth2/**",
            "/api/login",
            "/api/members/new",
            "/api/members/username",
            "/api/members/nickname",
            "/api/posts/main",
            "/api/posts/**",
            "/api/refreshToken",
            "/api/files/**"};

    private static final String[] blackList = {
            "/api/posts/new",
            "/api/oauth/members",
            "/api/posts/region/new",
            "/api/posts/independent/new",
            "/api/members/region",
            "/api/posts/update",
            "/api/chat/**",
            "/api/emitter/**",
            "/api/comments/**",
            "/api/members",
            "/api/alarms/**"
    };

    public static String[] getWhiteList() {
        return whiteList;
    }

    public static String[] getBlackList() {
        return blackList;
    }
}
