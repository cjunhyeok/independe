package community.independe.security.signature;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import org.springframework.security.core.userdetails.UserDetails;

// 대칭키
public class MacSecuritySigner extends SecuritySigner{

    @Override
    public String getJwtToken(UserDetails user, JWK jwk) throws JOSEException {
        // 토큰 발행
        MACSigner jwsSigner = new MACSigner(((OctetSequenceKey)jwk).toSecretKey()); // 시크릿 키를 이용해 Signer 생성
        return super.getJwtTokenInternal(jwsSigner, user, jwk); // Token 발행은 공통이므로 부모에게 전달
    }
}
