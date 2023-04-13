package community.independe.repository;

import community.independe.domain.favorite.FavoritePost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoritePostRepository extends JpaRepository<FavoritePost, Long> {
}
