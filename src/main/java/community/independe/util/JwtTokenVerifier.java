package community.independe.util;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import community.independe.domain.member.Member;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.security.service.MemberContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenVerifier {

    private final OctetSequenceKey jwk;
    private final MemberRepository memberRepository;

    public void verifyToken(String header) {

        if (header == null || !header.startsWith("Bearer ")) {
            // 토큰이 없거나 Bearer로 시작하지 않으면 다음 필터로 넘긴다.
            throw new CustomException(ErrorCode.ACCESS_TOKEN_NOT_FOUND);
        }
        // 순수 token 뽑아내기
        String token = header.replace("Bearer ", "");

        verify(token);
    }

    public void verifyToken(HttpServletRequest request) {

        String token = getHeaderAndToken(request);
        if (token == null) {
            throw new CustomException(ErrorCode.ACCESS_TOKEN_NOT_FOUND);
        }

        verify(token);
    }

    private String getHeaderAndToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            // 토큰이 없거나 Bearer로 시작하지 않으면 다음 필터로 넘긴다.
            throw new CustomException(ErrorCode.ACCESS_TOKEN_NOT_FOUND);
        }

        // 순수 token 뽑아내기
        String token = header.replace("Bearer ", "");
        return token;
    }

    private void verify(String token) {
        SignedJWT signedJWT;

        try {
            // token 파싱
            signedJWT = SignedJWT.parse(token);

            // 만료일 검증
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expirationTime.before(new Date())) {
                throw new CustomException(ErrorCode.ACCESS_TOKEN_EXPIRED);
            }

            // token 검증
            MACVerifier macVerifier = new MACVerifier(jwk.toSecretKey()); // 시크릿 키를 이용해 Verifier 생성

            boolean verify = signedJWT.verify(macVerifier);

            if (verify) {
                // verify가 true면 검증 성공 -> 인증 처리를 진행

                // 클레임 정보를 통해 Id, 권한 획득
                JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
                String username = jwtClaimsSet.getClaim("username").toString();

                if (username != null) {
                    authentication(username);
                }
            }
            else {
                throw new CustomException(ErrorCode.TOKEN_NOT_VERIFY);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private void authentication(String username) {
        Member findMember = memberRepository.findByUsername(username);

        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(findMember.getRole()));

        MemberContext memberContext = new MemberContext(findMember, roles);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(memberContext, null, memberContext.getAuthorities());

        // 인증 완료
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
