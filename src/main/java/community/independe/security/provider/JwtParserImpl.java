package community.independe.security.provider;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class JwtParserImpl implements JwtParser {

    @Override
    public JWT parse(String refreshToken) throws ParseException {
        return JWTParser.parse(refreshToken);
    }

    @Override
    public String getClaim(JWT jwt, String claim) throws ParseException {
        return (String) jwt.getJWTClaimsSet().getClaim(claim);
    }
}
