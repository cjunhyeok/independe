package community.independe.repository.file;

import community.independe.domain.file.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FilesRepository extends JpaRepository<Files, Long>, FilesRepositoryCustom {

    @Query(value = "select f from Files f left join fetch f.post" +
            " where f.post.id = :postId",
    countQuery = "select f from Files f" +
            " where f.post.id =: postId")
    List<Files> findAllFilesByPostId(@Param("postId") Long postId);
}
