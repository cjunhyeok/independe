package community.independe.security.service;

import community.independe.domain.member.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class MemberContext extends User implements OAuth2User {

    private final Member member;
    private Map<String, Object> attributes;

    public MemberContext(Member member, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
        super(member.getUsername(), member.getPassword(), authorities);
        this.member = member;
        this.attributes = attributes;
    }

    public MemberContext(Member member, Collection<? extends GrantedAuthority> authorities) {
        super(member.getUsername(), member.getPassword(), authorities);
        this.member = member;
    }

    public Member getMember() {
        return member;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
//        return attributes.get("sub");
        return null;
    }
}
