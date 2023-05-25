package community.independe.security.signature;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import community.independe.domain.member.Member;
import community.independe.security.service.MemberContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Date;
import java.util.Map;

public abstract class SecuritySigner {

    protected String getJwtTokenInternal(MACSigner jwsSigner, UserDetails user, JWK jwk) throws JOSEException {

        Member member = ((MemberContext) user).getMember();

        // jwk에서 알고리즘 정보와 keyId를 가져와 Header를 생성
        JWSHeader header = new JWSHeader.Builder((JWSAlgorithm)jwk.getAlgorithm()).keyID(jwk.getKeyID()).build();
        // Claim정보 생성
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject("user")
                .issuer("http://localhost:8080")
                .claim("username", member.getUsername()) // 사용자 Id
                .claim("authority", member.getRole()) // 사용자 권한
                .claim("nickname", member.getNickname()) // 사용자 닉네임
                .expirationTime(new Date(new Date().getTime() + 60 * 1000 * 10)) // 10분
                .build();
        SignedJWT signedJWT = new SignedJWT(header, jwtClaimsSet);
        signedJWT.sign(jwsSigner); // mac signer를 이용해 서명
        String jwtToken = signedJWT.serialize();

        return jwtToken;
    }

    protected String getTokenOAuth2(MACSigner jwsSigner, OAuth2User oAuth2User, JWK jwk) throws JOSEException {

        Map<String, Object> attributes = (Map<String, Object>) oAuth2User.getAttributes().get("response");
        Member member = ((MemberContext) oAuth2User).getMember();


        JWSHeader header = new JWSHeader.Builder((JWSAlgorithm)jwk.getAlgorithm()).keyID(jwk.getKeyID()).build();

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject("user")
                .issuer("http://localhost:8080")
                .claim("username", member.getUsername()) // 사용자 Id
                .claim("authority", member.getRole()) // 사용자 권한
                .claim("nickname", member.getNickname()) // 사용자 nickname
                .expirationTime(new Date(new Date().getTime() + 60 * 1000 * 10)) // 10분
                .build();
        SignedJWT signedJWT = new SignedJWT(header, jwtClaimsSet);
        signedJWT.sign(jwsSigner); // mac signer를 이용해 서명
        String jwtToken = signedJWT.serialize();

        return jwtToken;
    }

    public abstract String getJwtToken(UserDetails user, JWK jwk) throws JOSEException;

    public abstract String getOAuth2JwtToken(OAuth2User oAuth2User, JWK jwk) throws JOSEException;
}
