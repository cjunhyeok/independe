package community.independe;

import community.independe.domain.comment.Comment;
import community.independe.domain.keyword.Keyword;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.domain.video.Video;
import community.independe.repository.CommentRepository;
import community.independe.repository.keyword.KeywordRepository;
import community.independe.repository.MemberRepository;
import community.independe.repository.post.PostRepository;
import community.independe.repository.video.VideoRepository;
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
        private final VideoRepository videoRepository;
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

            for (int i = 1; i <= 22; i++) {
                postRepository.save(
                        Post.builder()
                                .title("AllFreeTitle" + i)
                                .content("AllFreeContent" + i)
                                .member(member)
                                .regionType(RegionType.ALL)
                                .regionPostType(RegionPostType.FREE)
                                .build()
                );
            }

            for (int i = 1; i <= 22; i++) {
                postRepository.save(
                        Post.builder()
                                .title("testIndependent" + i)
                                .content("independentContent" + i)
                                .member(member2)
                                .independentPostType(IndependentPostType.HEALTH)
                                .build()
                );
            }

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
            Keyword keyword7 = new Keyword("건강");
            keywordRepository.save(keyword7);
            Keyword keyword8 = new Keyword("청소");
            keywordRepository.save(keyword8);
            Keyword keyword9 = new Keyword("빨레");
            keywordRepository.save(keyword9);
            Keyword keyword10 = new Keyword("요리");
            keywordRepository.save(keyword10);
            Keyword keyword11 = new Keyword("울산");
            keywordRepository.save(keyword11);
            Keyword keyword12 = new Keyword("부산");
            keywordRepository.save(keyword12);
            Keyword keyword13 = new Keyword("경남");
            keywordRepository.save(keyword13);
            Keyword keyword14 = new Keyword("만남");
            keywordRepository.save(keyword14);

            Video cookVideo = Video.builder()
                    .videoTitle("CBUM")
                    .videoUrl("https://www.youtube.com/embed/trM50_Rk-qc")
                    .independentPostType(IndependentPostType.COOK)
                    .views(10)
                    .build();
            videoRepository.save(cookVideo);

            Video cookVideo2 = Video.builder()
                    .videoTitle("oneDay")
                    .videoUrl("https://www.youtube.com/embed/bLES_JyrmhQ")
                    .independentPostType(IndependentPostType.COOK)
                    .views(5)
                    .build();
            videoRepository.save(cookVideo2);

            Video cookVideo3 = Video.builder()
                    .videoTitle("oneDay2")
                    .videoUrl("https://www.youtube.com/embed/bLES_JyrmhQ")
                    .independentPostType(IndependentPostType.COOK)
                    .views(7)
                    .build();
            videoRepository.save(cookVideo3);

            Video cleanVideo = Video.builder()
                    .videoTitle("infinity challenge")
                    .videoUrl("https://www.youtube.com/embed/FVf-2DdFX80")
                    .independentPostType(IndependentPostType.CLEAN)
                    .views(10)
                    .build();
            videoRepository.save(cleanVideo);

            Video cleanVideo2 = Video.builder()
                    .videoTitle("bang kok")
                    .videoUrl("https://www.youtube.com/embed/8A4MwL_MiPE")
                    .independentPostType(IndependentPostType.CLEAN)
                    .views(2)
                    .build();
            videoRepository.save(cleanVideo2);

            Video cleanVideo3 = Video.builder()
                    .videoTitle("bang kok")
                    .videoUrl("https://www.youtube.com/embed/8A4MwL_MiPE")
                    .independentPostType(IndependentPostType.CLEAN)
                    .views(3)
                    .build();
            videoRepository.save(cleanVideo3);

            Video washVideo = Video.builder()
                    .videoTitle("relaxMan")
                    .videoUrl("https://www.youtube.com/embed/iYPFRvQ9Jr4")
                    .independentPostType(IndependentPostType.WASH)
                    .views(10)
                    .build();
            videoRepository.save(washVideo);

            Video washVideo2 = Video.builder()
                    .videoTitle("danuri")
                    .videoUrl("https://www.youtube.com/embed/Cr3zIEMk9Nk")
                    .independentPostType(IndependentPostType.WASH)
                    .views(12)
                    .build();
            videoRepository.save(washVideo2);

            Video washVideo3 = Video.builder()
                    .videoTitle("danuri2")
                    .videoUrl("https://www.youtube.com/embed/Cr3zIEMk9Nk")
                    .independentPostType(IndependentPostType.WASH)
                    .views(12)
                    .build();
            videoRepository.save(washVideo3);

            Video healthVideo1 = Video.builder()
                    .videoTitle("health1")
                    .videoUrl("https://www.youtube.com/embed/Cr3zIEMk9Nk")
                    .independentPostType(IndependentPostType.HEALTH)
                    .views(15)
                    .build();
            videoRepository.save(healthVideo1);

            Video healthVideo2 = Video.builder()
                    .videoTitle("health2")
                    .videoUrl("https://www.youtube.com/embed/Cr3zIEMk9Nk")
                    .independentPostType(IndependentPostType.HEALTH)
                    .views(15)
                    .build();
            videoRepository.save(healthVideo2);

            Video healthVideo3 = Video.builder()
                    .videoTitle("health3")
                    .videoUrl("https://www.youtube.com/embed/Cr3zIEMk9Nk")
                    .independentPostType(IndependentPostType.HEALTH)
                    .views(15)
                    .build();
            videoRepository.save(healthVideo3);

            Video etcVideo1 = Video.builder()
                    .videoTitle("etc1")
                    .videoUrl("https://www.youtube.com/embed/Cr3zIEMk9Nk")
                    .independentPostType(IndependentPostType.ETC)
                    .views(3)
                    .build();
            videoRepository.save(etcVideo1);

            Video etcVideo2 = Video.builder()
                    .videoTitle("etc2")
                    .videoUrl("https://www.youtube.com/embed/Cr3zIEMk9Nk")
                    .independentPostType(IndependentPostType.ETC)
                    .views(3)
                    .build();
            videoRepository.save(etcVideo2);

            Video etcVideo3 = Video.builder()
                    .videoTitle("etc3")
                    .videoUrl("https://www.youtube.com/embed/Cr3zIEMk9Nk")
                    .independentPostType(IndependentPostType.ETC)
                    .views(3)
                    .build();
            videoRepository.save(etcVideo3);
        }
    }
}
