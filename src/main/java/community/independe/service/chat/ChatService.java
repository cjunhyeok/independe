package community.independe.service.chat;

public interface ChatService {

    Long saveChat(String message, Long senderId, Long receiverId);
}
