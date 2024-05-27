package community.independe.security.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import community.independe.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignatureConfig {

    private final MemberRepository memberRepository;
    private final String secretKey;
    private final byte[] secretKeyBytes;

    @Autowired
    public SignatureConfig(MemberRepository memberRepository, @Value("${app.auth.secret-key}") String secretKey) {
        this.memberRepository = memberRepository;
        this.secretKey = secretKey;
        this.secretKeyBytes = secretKey.getBytes();
    }

//    @Bean
//    public OctetSequenceKey octetSequenceKey() throws JOSEException {
//        OctetSequenceKey macKey = new OctetSequenceKeyGenerator(256)
//                .keyID("macKey")
//                .algorithm(JWSAlgorithm.HS256)
//                .generate();
//
//        return macKey;
//    }

    @Bean
    public OctetSequenceKey octetSequenceKey() {
        return new OctetSequenceKey
                .Builder(secretKeyBytes)
                .keyID("macKey")
                .algorithm(JWSAlgorithm.HS256)
                .build();
    }
}
