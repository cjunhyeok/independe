package community.independe.domain.chat;

import community.independe.domain.manytomany.BaseManyToManyEntity;
import community.independe.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRead extends BaseManyToManyEntity {

    @Id @GeneratedValue
    @Column(name = "chat_read_id")
    private Long id;

    private boolean isRead;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Builder
    public ChatRead(boolean isRead, Member member, Chat chat) {
        this.isRead = isRead;
        this.member = member;
        this.chat = chat;
    }
}
