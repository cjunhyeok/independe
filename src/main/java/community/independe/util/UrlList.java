package community.independe.util;

import java.util.HashMap;
import java.util.Map;

public abstract class UrlList {

    private static final Map<String, String[]> whiteList;
    private static final Map<String, String[]> blackList;

    static {
        whiteList = new HashMap<>();
        String[] whiteGetList = {
                "/",
                "/actuator/**",
                "/ws",
                "/ws/**",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/api/posts/**",
                "/api/videos",
                "/api/files/**"
        };
        whiteList.put("GET", whiteGetList);

        String[] whitePostList = {
                "/api/members/new",
                "/api/member/login",
                "/api/members/username",
                "/api/members/nickname",
                "/api/refreshToken",
        };
        whiteList.put("POST", whitePostList);
    }

    static {
        blackList = new HashMap<>();

        String[] blackGetList = {
                "/api/member",
                "/api/member/post",
                "/api/member/comment",
                "/api/alarms",
                "/api/chat/**",
                "/api/emitter/subscribe",
                "/api/favoritePost",
                "/api/recommendComment",
                "/api/recommendPost"
        };
        blackList.put("GET", blackGetList);

        String[] blackPostList = {
                "/api/members/region",
                "/api/posts/**",
                "/api/comments/**",
                "/api/chat/**",
                "/api/favoritePost/**",
                "/api/recommendComment/**",
                "/api/recommendPost/**",
                "/api/reportPost/**"
        };
        blackList.put("POST", blackPostList);

        String[] blackPutList = {
                "/api/oauth/members",
                "/api/members",
                "/api/members/password",
                "/api/posts/**",
        };
        blackList.put("PUT", blackPutList);

        String[] blackDeleteList = {
                "/api/posts/**",
        };
        blackList.put("DELETE", blackDeleteList);
    }

    public static Map<String, String[]> getWhiteList() {
        return whiteList;
    }

    public static String[] getWhiteGetList() {
        return whiteList.get("GET");
    }

    public static String[] getWhitePostList() {
        return whiteList.get("POST");
    }

    public static Map<String, String[]> getBlackList() {
        return blackList;
    }

    public static String[] getBlackGetList() {
        return blackList.get("GET");
    }

    public static String[] getBlackPostList() {
        return blackList.get("POST");
    }

    public static String[] getBlackPutList() {
        return blackList.get("PUT");
    }

    public static String[] getBlackDeleteList() {
        return blackList.get("DELETE");
    }
}
