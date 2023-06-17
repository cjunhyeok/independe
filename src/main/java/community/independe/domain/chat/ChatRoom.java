package community.independe.domain.chat;

import community.independe.domain.BaseEntity;
import community.independe.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "chat_room_id")
    private Long id;
    private String title;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender; // 발신자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiver; // 수신자

    @Builder
    public ChatRoom(String title, Member sender, Member receiver) {
        this.title = title;
        this.sender = sender;
        this.receiver = receiver;
    }
}
