package community.independe.repository.token;

import community.independe.domain.token.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    RefreshToken findByRefreshToken(String refreshToken);
    RefreshToken findByUsername(String username);
}
