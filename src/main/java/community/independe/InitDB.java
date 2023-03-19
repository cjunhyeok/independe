package community.independe;

import community.independe.domain.comment.Comment;
import community.independe.domain.keyword.Keyword;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.repository.CommentRepository;
import community.independe.repository.KeywordRepository;
import community.independe.repository.MemberRepository;
import community.independe.repository.PostRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitDB {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit();
    }


    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;
        private final MemberRepository memberRepository;
        private final PostRepository postRepository;
        private final CommentRepository commentRepository;
        private final KeywordRepository keywordRepository;
        private final PasswordEncoder passwordEncoder;

        public void dbInit() {

            Member member = Member.builder()
                    .username("id1")
                    .password(passwordEncoder.encode("Wnsgur1214@"))
                    .nickname("nick1")
                    .role("ROLE_USER")
                    .build();
            memberRepository.save(member);

            Member member2 = Member.builder()
                    .username("id2")
                    .password(passwordEncoder.encode("Wnsgur1214@"))
                    .nickname("nick2")
                    .role("ROLE_USER")
                    .build();
            memberRepository.save(member2);

            Post regionPost = Post.builder()
                    .title("regionTitle")
                    .content("regionContent")
                    .member(member)
                    .regionType(RegionType.ALL)
                    .regionPostType(RegionPostType.FREE)
                    .build();
            postRepository.save(regionPost);

            Post regionPost2 = Post.builder()
                    .title("regionTitle2")
                    .content("regionContent2")
                    .member(member)
                    .regionType(RegionType.PUSAN)
                    .regionPostType(RegionPostType.MARKET)
                    .build();
            postRepository.save(regionPost2);

            Post regionPost3 = Post.builder()
                    .title("regionTitle")
                    .content("regionContent")
                    .member(member)
                    .regionType(RegionType.ULSAN)
                    .regionPostType(RegionPostType.MEET)
                    .build();
            postRepository.save(regionPost3);

            Post independentPost = Post.builder()
                    .title("independentTitle")
                    .content("independentContent")
                    .member(member)
                    .independentPostType(IndependentPostType.COOK)
                    .build();
            postRepository.save(independentPost);

            Post independentPost2 = Post.builder()
                    .title("independentTitle2")
                    .content("independentContent")
                    .member(member)
                    .independentPostType(IndependentPostType.CLEAN)
                    .build();
            postRepository.save(independentPost2);

            Post independentPost3 = Post.builder()
                    .title("independentTitle3")
                    .content("independentContent")
                    .member(member)
                    .independentPostType(IndependentPostType.WASH)
                    .build();
            postRepository.save(independentPost3);

            Post independentPost4 = Post.builder()
                    .title("independentTitle4")
                    .content("independentContent")
                    .member(member)
                    .independentPostType(IndependentPostType.WASH)
                    .build();
            postRepository.save(independentPost4);

            Post independentPost5 = Post.builder()
                    .title("independentTitle5")
                    .content("independentContent")
                    .member(member)
                    .independentPostType(IndependentPostType.WASH)
                    .build();
            postRepository.save(independentPost5);

            Post independentPost6 = Post.builder()
                    .title("independentTitle6")
                    .content("independentContent")
                    .member(member)
                    .independentPostType(IndependentPostType.WASH)
                    .build();
            postRepository.save(independentPost6);

            Post independentPost7 = Post.builder()
                    .title("independentTitle6")
                    .content("independentContent")
                    .member(member)
                    .independentPostType(IndependentPostType.WASH)
                    .build();
            postRepository.save(independentPost7);

            Post independentPost8 = Post.builder()
                    .title("independentTitle6")
                    .content("independentContent")
                    .member(member)
                    .independentPostType(IndependentPostType.WASH)
                    .build();
            postRepository.save(independentPost8);

            Comment comment1 = Comment.builder()
                    .content("comment1")
                    .member(member)
                    .post(independentPost)
                    .build();
            commentRepository.save(comment1);

            Comment comment2 = Comment.builder()
                    .content("comment2")
                    .member(member)
                    .post(independentPost)
                    .build();
            commentRepository.save(comment2);

            Comment comment3 = Comment.builder()
                    .content("comment3")
                    .member(member)
                    .post(regionPost)
                    .build();
            commentRepository.save(comment3);

            Comment comment4 = Comment.builder()
                    .content("comment4")
                    .member(member)
                    .post(independentPost)
                    .parent(comment2)
                    .build();
            commentRepository.save(comment4);

            Keyword keyword = new Keyword("자취");
            keywordRepository.save(keyword);
            Keyword keyword2 = new Keyword("자취");
            keywordRepository.save(keyword2);
            Keyword keyword3 = new Keyword("생활");
            keywordRepository.save(keyword3);
            Keyword keyword4 = new Keyword("생활");
            keywordRepository.save(keyword4);
            Keyword keyword5 = new Keyword("생활");
            keywordRepository.save(keyword5);
            Keyword keyword6 = new Keyword("꿀팁");
            keywordRepository.save(keyword6);

        }
    }
}
