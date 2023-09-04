package community.independe.security.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.gen.OctetSequenceKeyGenerator;
import community.independe.repository.MemberRepository;
import community.independe.security.signature.MacSecuritySigner;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SignatureConfig {

    private final MemberRepository memberRepository;

    @Bean
    public MacSecuritySigner macSecuritySigner() {
        return new MacSecuritySigner(memberRepository);
    }

    @Bean
    public OctetSequenceKey octetSequenceKey() throws JOSEException {
        OctetSequenceKey macKey = new OctetSequenceKeyGenerator(256)
                .keyID("macKey")
                .algorithm(JWSAlgorithm.HS256)
                .generate();

        return macKey;
    }
}
