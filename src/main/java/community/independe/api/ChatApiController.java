package community.independe.api;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import community.independe.api.dtos.chat.Message;
import community.independe.domain.member.Member;
import community.independe.repository.MemberRepository;
import community.independe.security.service.MemberContext;
import community.independe.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatApiController {

    private final ChatService chatService;
    private final OctetSequenceKey jwk;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate simpMessagingTemplate; // 특정 상대에게 메시지를 보내기 위한 객체

    @MessageMapping("/private-message")
    public Message receivePrivateMessage(@Payload Message message, @Header(name = "Authorization") String header){

        boolean verify = verifyToken(header);
        if (verify == false) {
            throw new RuntimeException();
        }
        Member loginMember = ((MemberContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getMember();

        message.setSenderNickname(loginMember.getNickname());

        simpMessagingTemplate.convertAndSendToUser(message.getChatRoomId().toString(),"/private",message);
        Long savedChat = chatService.saveChat(loginMember.getId(), message.getReceiverId(), message.getMessage(), false);

        return message;
    }

    private boolean verifyToken(String header) {

        if (header == null || !header.startsWith("Bearer ")) {
            // 토큰이 없거나 Bearer로 시작하지 않으면 다음 필터로 넘긴다.
            return false;
        }


        // 순수 token 뽑아내기
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
                    return true;
                }
            }
            else {
                return false;
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
