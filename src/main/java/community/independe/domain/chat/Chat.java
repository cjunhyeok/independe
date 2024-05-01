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
public class Chat extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "chat_id")
    private Long id;
    private String message; // 메시지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 발신자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @Builder
    public Chat(String message, Member member, ChatRoom chatRoom) {
        this.message = message;
        this.member = member;
        this.chatRoom = chatRoom;
    }
}
