package community.independe.repository;

import community.independe.domain.manytomany.FavoritePost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoritePostRepository extends JpaRepository<FavoritePost, Long> {
}
