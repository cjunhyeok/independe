package community.independe.repository.chat;

import community.independe.domain.chat.ChatRead;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatReadRepository extends JpaRepository<ChatRead, Long> {
}
