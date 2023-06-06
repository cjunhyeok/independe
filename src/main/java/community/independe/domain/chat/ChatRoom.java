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
    @JoinColumn(name = "firstParticipation_id")
    private Member firstParticipation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secondParticipation_id")
    private Member secondParticipation;

    @Builder
    public ChatRoom(String title, Member firstParticipation, Member secondParticipation) {
        this.title = title;
        this.firstParticipation = firstParticipation;
        this.secondParticipation = secondParticipation;
    }
}
