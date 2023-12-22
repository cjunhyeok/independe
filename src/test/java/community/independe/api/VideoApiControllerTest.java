package community.independe.api;

import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.video.Video;
import community.independe.repository.video.VideoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VideoApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private VideoRepository videoRepository;

    @Test
    void findVideosTest() throws Exception {
        // given
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

        // when
        ResultActions perform = mockMvc.perform(get("/api/videos"));

        // then
        perform.andExpect(status().isOk());
    }
}
