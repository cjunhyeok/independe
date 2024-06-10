package community.independe.security.signature;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import community.independe.domain.member.Member;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.security.service.MemberContext;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Date;
import java.util.Map;

public abstract class SecuritySigner {

    private final MemberRepository memberRepository;
    protected final JWK jwk;

    public SecuritySigner(MemberRepository memberRepository, JWK jwk) {
        this.memberRepository = memberRepository;
        this.jwk = jwk;
    }

    protected String getJwtTokenInternal(MACSigner jwsSigner, String username) throws JOSEException {

        Member findMember = memberRepository.findByUsername(username);

        // jwk에서 알고리즘 정보와 keyId를 가져와 Header를 생성
        JWSHeader header = new JWSHeader.Builder((JWSAlgorithm)jwk.getAlgorithm()).keyID(jwk.getKeyID()).build();
        // Claim정보 생성
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject("user")
                .issuer("http://spring:8080")
                .claim("username", findMember.getUsername())
                .claim("authority", findMember.getRole()) // 사용자 권한
                .claim("nickname", findMember.getNickname()) // 사용자 닉네임
                .claim("region", findMember.getRegion()) // 사용자 지
                .expirationTime(new Date(new Date().getTime() + 60 * 1000 * 60)) // 60분
                .build();
        SignedJWT signedJWT = new SignedJWT(header, jwtClaimsSet);
        signedJWT.sign(jwsSigner); // mac signer를 이용해 서명
        String jwtToken = signedJWT.serialize();

        return jwtToken;
    }

    protected String getTokenOAuth2(MACSigner jwsSigner, OAuth2User oAuth2User) throws JOSEException {

        Map<String, Object> attributes = (Map<String, Object>) oAuth2User.getAttributes().get("response");
        Long loginMemberId = ((MemberContext) oAuth2User).getMemberId();

        Member findMember = memberRepository.findById(loginMemberId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );


        JWSHeader header = new JWSHeader.Builder((JWSAlgorithm)jwk.getAlgorithm()).keyID(jwk.getKeyID()).build();

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject("user")
                .issuer("http://localhost:8080")
                .claim("username", findMember.getUsername()) // 사용자 Id
                .claim("authority", findMember.getRole()) // 사용자 권한
                .claim("nickname", findMember.getNickname()) // 사용자 nickname
                .expirationTime(new Date(new Date().getTime() + 60 * 1000 * 60)) // 60분
                .build();
        SignedJWT signedJWT = new SignedJWT(header, jwtClaimsSet);
        signedJWT.sign(jwsSigner); // mac signer를 이용해 서명
        String jwtToken = signedJWT.serialize();

        return jwtToken;
    }

    protected String getRefreshToken(MACSigner jwsSigner, String username) throws JOSEException {

        Member findMember = memberRepository.findByUsername(username);

        // jwk에서 알고리즘 정보와 keyId를 가져와 Header를 생성
        JWSHeader header = new JWSHeader.Builder((JWSAlgorithm)jwk.getAlgorithm()).keyID(jwk.getKeyID()).build();
        // Claim정보 생성
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject("user")
                .issuer("http://localhost:8080")
                .claim("username", findMember.getUsername())
                .claim("authority", findMember.getRole()) // 사용자 권한
                .claim("nickname", findMember.getNickname()) // 사용자 닉네임
                .claim("region", findMember.getRegion()) // 사용자 지역
                .expirationTime(new Date(new Date().getTime() + (7 * 24 * 60 * 60 * 1000))) // 1주일
                .build();
        SignedJWT signedJWT = new SignedJWT(header, jwtClaimsSet);
        signedJWT.sign(jwsSigner); // mac signer를 이용해 서명
        String jwtToken = signedJWT.serialize();

        return jwtToken;
    }

    public abstract String getJwtToken(String username) throws JOSEException;

    public abstract String getOAuth2JwtToken(OAuth2User oAuth2User) throws JOSEException;

    public abstract String getRefreshJwtToken(String username) throws JOSEException;
}
