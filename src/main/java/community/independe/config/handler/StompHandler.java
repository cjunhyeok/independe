package community.independe.config.handler;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import community.independe.domain.member.Member;
import community.independe.repository.MemberRepository;
import community.independe.security.service.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final OctetSequenceKey jwk;
    private final MemberRepository memberRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String header = null;

        if (accessor.getCommand() == StompCommand.CONNECT) {
            header = accessor.getFirstNativeHeader("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                // 토큰이 없거나 Bearer로 시작하지 않으면 다음 필터로 넘긴다.
                throw new IllegalArgumentException("no token");
            }

            String token = header.replace("Bearer ", "");

            SignedJWT signedJWT;

            try {
                // token 파싱
                signedJWT = SignedJWT.parse(token);

                // token 검증
                MACVerifier macVerifier = new MACVerifier(jwk.toSecretKey()); // 시크릿 키를 이용해 Verifier 생성
                boolean verify = signedJWT.verify(macVerifier);

                if (verify) {
                    // verify가 true면 검증 성공 -> 인증 처리를 진행

                    // 클레임 정보를 통해 Id, 권한 획득
                    JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
                    String username = jwtClaimsSet.getClaim("username").toString();

                    if (username != null) {
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
                return message;
            } catch (ParseException e) {
                throw new RuntimeException(e);
            } catch (JOSEException e) {
                throw new RuntimeException(e);
            }
        }
        return message;
    }
}

