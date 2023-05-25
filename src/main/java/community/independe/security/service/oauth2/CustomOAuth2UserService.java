package community.independe.security.service.oauth2;

import community.independe.domain.member.Member;
import community.independe.domain.member.oauth2.NaverUser;
import community.independe.domain.member.oauth2.ProviderUser;
import community.independe.repository.MemberRepository;
import community.independe.security.service.MemberContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest); // 인가 서버와 통신하여 사용자 정보를 가져온다.

        ProviderUser providerUser = providerUser(clientRegistration, oAuth2User);

        // 회원가입
        Member savedMember = register(providerUser, userRequest);
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(savedMember.getRole()));

        MemberContext memberContext = new MemberContext(savedMember, roles, oAuth2User.getAttributes());

        return memberContext;
    }

    public Member register(ProviderUser providerUser, OAuth2UserRequest userRequest) {

        Member findMember = memberRepository.findByUsername(providerUser.getUsername());

        if (findMember == null) {

            Member member = Member.builder()
                    .registrationId(providerUser.getId())
                    .username(providerUser.getUsername())
                    .password(passwordEncoder.encode(providerUser.getPassword()))
                    .email(providerUser.getEmail())
                    .role(providerUser.getAuthorities().get(0).toString())
                    .nickname(providerUser.getNickname())
                    .provider(providerUser.getProvider())
                    .build();

            Member savedMember = memberRepository.save(member);

            return savedMember;
        } else {
            return findMember;
        }
    }

    public ProviderUser providerUser(ClientRegistration clientRegistration, OAuth2User oAuth2User) {

        String registrationId = clientRegistration.getRegistrationId();
        if (registrationId.equals("naver")) {
            return new NaverUser(oAuth2User, clientRegistration);
        } else {
            throw new IllegalArgumentException(registrationId + " not exist");
        }

    }
}
