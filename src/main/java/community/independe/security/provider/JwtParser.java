package community.independe.security.provider;

import com.nimbusds.jwt.JWT;

import java.text.ParseException;

public interface JwtParser {

    JWT parse(String refreshToken) throws ParseException;
    String getClaim(JWT jwt, String claim) throws ParseException;
}
