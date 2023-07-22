package community.independe;

import community.independe.domain.comment.Comment;
import community.independe.domain.keyword.Keyword;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.domain.video.Video;
import community.independe.repository.comment.CommentRepository;
import community.independe.repository.keyword.KeywordRepository;
import community.independe.repository.MemberRepository;
import community.independe.repository.post.PostRepository;
import community.independe.repository.video.VideoRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("dev")
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
                    .password(passwordEncoder.encode("abc12!"))
                    .nickname("nick1")
                    .role("ROLE_USER")
                    .build();
            memberRepository.save(member);

            Member member2 = Member.builder()
                    .username("id2")
                    .password(passwordEncoder.encode("zcxvqew!@"))
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

//            Keyword keyword = new Keyword("자취");
            Keyword keyword = Keyword.builder()
                    .keyword("자취")
                    .build();
            keywordRepository.save(keyword);
            Keyword keyword2 = Keyword.builder()
                    .keyword("자취")
                    .build();
            keywordRepository.save(keyword2);
            Keyword keyword3 = Keyword.builder()
                    .keyword("생활")
                    .build();
            keywordRepository.save(keyword3);
            Keyword keyword4 = Keyword.builder()
                    .keyword("생활")
                    .build();
            keywordRepository.save(keyword4);
            Keyword keyword5 = Keyword.builder()
                    .keyword("생활")
                    .build();
            keywordRepository.save(keyword5);
            Keyword keyword6 = Keyword.builder()
                    .keyword("꿀팁")
                    .build();
            keywordRepository.save(keyword6);
            Keyword keyword7 = Keyword.builder()
                    .keyword("건강")
                    .build();
            keywordRepository.save(keyword7);
            Keyword keyword8 = Keyword.builder()
                    .keyword("청소")
                    .build();
            keywordRepository.save(keyword8);
            Keyword keyword9 = Keyword.builder()
                    .keyword("빨레")
                    .build();
            keywordRepository.save(keyword9);
            Keyword keyword10 = Keyword.builder()
                    .keyword("요리")
                    .build();
            keywordRepository.save(keyword10);
            Keyword keyword11 = Keyword.builder()
                    .keyword("울산")
                    .build();
            keywordRepository.save(keyword11);
            Keyword keyword12 = Keyword.builder()
                    .keyword("부산")
                    .build();
            keywordRepository.save(keyword12);
            Keyword keyword13 = Keyword.builder()
                    .keyword("경남")
                    .build();
            keywordRepository.save(keyword13);
            Keyword keyword14 = Keyword.builder()
                    .keyword("만남")
                    .build();
            keywordRepository.save(keyword14);

            Video cookVideo = Video.builder()
                    .videoTitle("락스, 과탄산 세면대에 그만 부으세요! 화학용품없이 세면대, 배수구 시원하게 뚫어주는 살림 꿀템!")
                    .videoUrl("https://www.youtube.com/embed/_mylUO5QIZM")
                    .materName("청소알려주는남자CleaningMan")
                    .independentPostType(IndependentPostType.CLEAN)
                    .views(2600)
                    .build();
            videoRepository.save(cookVideo);

            Video cookVideo1 = Video.builder()
                    .videoTitle("자취생 오징어볶음 & 소면 만드는 영상")
                    .videoUrl("https://www.youtube.com/embed/ZmqsGQn-1-w")
                    .materName("자취요리TV")
                    .independentPostType(IndependentPostType.COOK)
                    .views(1100)
                    .build();
            videoRepository.save(cookVideo1);

            Video cookVideo2 = Video.builder()
                    .videoTitle("한달만에 건강하게 5kg 빼는 비법 3가지 [40대다이어트,50대다이어트,중년다이어트,중년살빼기,나잇살]")
                    .videoUrl("https://www.youtube.com/embed/4U1Fz_FW2TA")
                    .materName("건강운동TV")
                    .independentPostType(IndependentPostType.HEALTH)
                    .views(8400)
                    .build();
            videoRepository.save(cookVideo2);

            Video cookVideo3 = Video.builder()
                    .videoTitle("청소 업체에서 비밀로 하는 초간단 화장실 청소 방법, 10분이면 충분합니다!")
                    .videoUrl("https://www.youtube.com/embed/MsXhRTw-v랴")
                    .materName("살림톡")
                    .independentPostType(IndependentPostType.CLEAN)
                    .views(10000)
                    .build();
            videoRepository.save(cookVideo3);

            Video cookVideo4 = Video.builder()
                    .videoTitle("대부분 놓치는 근육 성장을 방해하는 습관 4가지 ee")
                    .videoUrl("https://www.youtube.com/embed/bzSB9ghOhrk")
                    .materName("헬마드(헬스 건강 정보)")
                    .independentPostType(IndependentPostType.HEALTH)
                    .views(54000)
                    .build();
            videoRepository.save(cookVideo4);

            Video cookVideo5 = Video.builder()
                    .videoTitle("정사각형 원룸 가구배치 인테리어 꿀팁, 이거 아직도 몰라요? l 6평 원룸 배치방법")
                    .videoUrl("https://www.youtube.com/embed/Q1LsNKg8DWs")
                    .materName("인테리어티쳐")
                    .independentPostType(IndependentPostType.ETC)
                    .views(64000)
                    .build();
            videoRepository.save(cookVideo5);

            Video cookVideo6 = Video.builder()
                    .videoTitle("참치캔 하나로 만드는 참치죽! 정성만 있으면 누구나 끓일 수 있다")
                    .videoUrl("https://www.youtube.com/embed/DzGa0anw7so")
                    .materName("자취하는 아저씨")
                    .independentPostType(IndependentPostType.COOK)
                    .views(270000)
                    .build();
            videoRepository.save(cookVideo6);

            Video cookVideo7 = Video.builder()
                    .videoTitle("먹다 남은 식은 치킨으로 만드는 3가지 요리!")
                    .videoUrl("https://www.youtube.com/embed/agYjq739Qf4")
                    .materName("자취하는 아저씨")
                    .independentPostType(IndependentPostType.COOK)
                    .views(280000)
                    .build();
            videoRepository.save(cookVideo7);

            Video cookVideo8 = Video.builder()
                    .videoTitle("[초간단 자취요리] 고깃집보다 더 맛있는 고깃집 된장찌개 / Doenjang-jjigae")
                    .videoUrl("https://www.youtube.com/embed/zZnn7bXaCLc")
                    .materName("자취하는 아저씨")
                    .independentPostType(IndependentPostType.COOK)
                    .views(52000)
                    .build();
            videoRepository.save(cookVideo8);

            Video cookVideo9 = Video.builder()
                    .videoTitle("세탁기에 물티슈 2장을 넣으면 생기는 놀라운 변화를 발견했습니다 [생활꿀팁/아이디어/보풀/강아지털/고양이털/세탁]")
                    .videoUrl("https://www.youtube.com/embed/saieurwMPbo")
                    .materName("살림톡")
                    .independentPostType(IndependentPostType.WASH)
                    .views(11000)
                    .build();
            videoRepository.save(cookVideo9);

            Video cookVideo10 = Video.builder()
                    .videoTitle("만약 집에 씽크대가 있으면 꼭 봐야 할 영상!(놀라운 효과 증명) [싱크대/배수구/청소/냄새/악취/제거]")
                    .videoUrl("https://www.youtube.com/embed/2p96FCJHjUM")
                    .materName("살림톡")
                    .independentPostType(IndependentPostType.CLEAN)
                    .views(19000)
                    .build();
            videoRepository.save(cookVideo10);

            Video cookVideo11 = Video.builder()
                    .videoTitle("직원들만 몰래 산다는 다이소 꿀템 6가지, 없어서 못삽니다! / 다이소 추천템, 청소템, 주방용품")
                    .videoUrl("https://www.youtube.com/embed/qpkdUVMigAk")
                    .materName("살림톡")
                    .independentPostType(IndependentPostType.CLEAN)
                    .views(37000)
                    .build();
            videoRepository.save(cookVideo11);

            Video cookVideo12 = Video.builder()
                    .videoTitle("아침 식단만 '이렇게' 바꿔보세요!! '근육량' 잘 늘고 체지방은 줄어듭니다")
                    .videoUrl("https://www.youtube.com/embed/79EjYyZMAoI")
                    .materName("헬마드 (헬스 건강 정보)")
                    .independentPostType(IndependentPostType.HEALTH)
                    .views(730000)
                    .build();
            videoRepository.save(cookVideo12);

            Video cookVideo13 = Video.builder()
                    .videoTitle("11자복근 복부 최고의 운동 [복근 핵매운맛]")
                    .videoUrl("https://www.youtube.com/embed/PjGcOP-TQPE")
                    .materName("Thankyou BUBU")
                    .independentPostType(IndependentPostType.HEALTH)
                    .views(10230000)
                    .build();
            videoRepository.save(cookVideo13);

            Video cookVideo14 = Video.builder()
                    .videoTitle("세탁기 청소 초간단 방법!(세균 범벅 빨래는 이제 그만...)")
                    .videoUrl("https://www.youtube.com/embed/3zMzMcqYGV4")
                    .materName("살림톡")
                    .independentPostType(IndependentPostType.CLEAN)
                    .views(770000)
                    .build();
            videoRepository.save(cookVideo14);

            Video cookVideo15 = Video.builder()
                    .videoTitle("간장 계란밥 더 맛있게 만들기! 버터 간장계란밥")
                    .videoUrl("https://www.youtube.com/embed/Qw1Ak9CY-kg")
                    .materName("자취하는 아저씨")
                    .independentPostType(IndependentPostType.COOK)
                    .views(1200)
                    .build();
            videoRepository.save(cookVideo15);

            Video cookVideo16 = Video.builder()
                    .videoTitle("이건 너무 쉽잖아?")
                    .videoUrl("https://www.youtube.com/embed/u9Xz-qCuTYQ")
                    .materName("자취요리신 simple cooking")
                    .independentPostType(IndependentPostType.COOK)
                    .views(320000)
                    .build();
            videoRepository.save(cookVideo16);

            Video cookVideo18 = Video.builder()
                    .videoTitle("세탁 세제는 얼만큼 넣어야 정량이지?\uD83D\uDC9A")
                    .videoUrl("https://www.youtube.com/embed/9V43Fl976MA")
                    .materName("자취요리신 simple cooking")
                    .independentPostType(IndependentPostType.WASH)
                    .views(30000)
                    .build();
            videoRepository.save(cookVideo18);

            Video cookVideo19 = Video.builder()
                    .videoTitle("유튜브 옷 정리 꿀팁 - 최최종_진짜_final ver. (이거 하나면 끝!) | 하루 뚝딱 인테리어 ep.03")
                    .videoUrl("https://www.youtube.com/embed/V-NfX7To3lg")
                    .materName("오늘의집")
                    .independentPostType(IndependentPostType.WASH)
                    .views(490000)
                    .build();
            videoRepository.save(cookVideo19);

            Video cookVideo20 = Video.builder()
                    .videoTitle("집 구할 때 알아야 하는 10단계 절차 총정리 / 자취방 구하기 EP.00")
                    .videoUrl("https://www.youtube.com/embed/kvoIs5xuOpA")
                    .materName("안선생")
                    .independentPostType(IndependentPostType.ETC)
                    .views(270000)
                    .build();
            videoRepository.save(cookVideo20);

            Video cookVideo21 = Video.builder()
                    .videoTitle("다이소, 품절되기 전에 꼭 사야 하는 추천템 6가지 소개해드립니다\uD83D\uDC9A")
                    .videoUrl("https://www.youtube.com/embed/gXc6MbmiBLA")
                    .materName("살림톡")
                    .independentPostType(IndependentPostType.ETC)
                    .views(5900)
                    .build();
            videoRepository.save(cookVideo21);
        }
    }
}
