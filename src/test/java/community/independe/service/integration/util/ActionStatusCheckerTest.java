package community.independe.service.integration.util;

import community.independe.IntegrationTestSupporter;
import community.independe.domain.comment.Comment;
import community.independe.domain.manytomany.FavoritePost;
import community.independe.domain.manytomany.RecommendComment;
import community.independe.domain.manytomany.RecommendPost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.repository.MemberRepository;
import community.independe.repository.comment.CommentRepository;
import community.independe.repository.manytomany.FavoritePostRepository;
import community.independe.repository.manytomany.RecommendCommentRepository;
import community.independe.repository.manytomany.RecommendPostRepository;
import community.independe.repository.manytomany.ReportPostRepository;
import community.independe.repository.post.PostRepository;
import community.independe.service.util.ActionStatusChecker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@Transactional
public class ActionStatusCheckerTest extends IntegrationTestSupporter {

    @Autowired
    private ActionStatusChecker actionStatusChecker;
    @Autowired
    private RecommendCommentRepository recommendCommentRepository;
    @Autowired
    private RecommendPostRepository recommendPostRepository;
    @Autowired
    private FavoritePostRepository favoritePostRepository;
    @Autowired
    private ReportPostRepository repository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("댓글 추천 엔티티의 추천 여부 반환 시 True 를 반환한다.")
    void isRecommendCommentIsTrueTest() {
        // given
        Member member = createMember();
        Post post = createPost(member);
        Comment comment = createComment(member, post);
        createRecommendComment(member, comment);

        // when
        boolean isRecommend = actionStatusChecker.isRecommendComment(comment.getId(), post.getId(), member.getId());

        // then
        assertThat(isRecommend).isTrue();
    }

    @Test
    @DisplayName("댓글 추천 엔티티의 추천 여부 반환 시 False 를 반환한다.")
    void isRecommendCommentIsFalseTest() {
        // given
        Member member = createMember();
        Post post = createPost(member);
        Comment comment = createComment(member, post);
        RecommendComment recommendComment = createRecommendComment(member, comment);
        recommendComment.updateIsRecommend();

        // when
        boolean isRecommend = actionStatusChecker.isRecommendComment(comment.getId(), post.getId(), member.getId());

        // then
        assertThat(isRecommend).isFalse();
    }


    @Test
    @DisplayName("댓글 추천 엔티티 조회 후 null 이면 false 를 반환한다.")
    void isRecommendCommentFalseTest() {
        // given
        Member member = createMember();
        Post post = createPost(member);
        Comment comment = createComment(member, post);
        createRecommendComment(member, comment);

        // when
        boolean isRecommend = actionStatusChecker.isRecommendComment(comment.getId() + 1L, post.getId(), member.getId());

        // then
        assertThat(isRecommend).isFalse();
    }

    @Test
    @DisplayName("memberId 가 null 이면 false 를 반환한다.")
    void isRecommendCommentMemberFalseTest() {
        // given

        // when
        boolean isRecommend = actionStatusChecker.isRecommendComment(null, null, null);

        // then
        assertThat(isRecommend).isFalse();
    }

    @Test
    @DisplayName("게시글 추천 엔티티의 추천 여부 반환 시 True 를 반환한다.")
    void isRecommendPostIsTrueTest() {
        // given
        Member member = createMember();
        Post post = createPost(member);
        createRecommendPost(member, post);

        // when
        boolean isRecommend = actionStatusChecker.isRecommend(post.getId(), member.getId());

        // then
        assertThat(isRecommend).isTrue();
    }

    @Test
    @DisplayName("게시글 추천 엔티티의 추천 여부 반환 시 False 를 반환한다.")
    void isRecommendPostIsFalseTest() {
        // given
        Member member = createMember();
        Post post = createPost(member);
        RecommendPost recommendPost = createRecommendPost(member, post);
        recommendPost.updateIsRecommend();

        // when
        boolean isRecommend = actionStatusChecker.isRecommend(post.getId(), member.getId());

        // then
        assertThat(isRecommend).isFalse();
    }


    @Test
    @DisplayName("게시글 추천 엔티티 조회 후 null 이면 false 를 반환한다.")
    void isRecommendPostFalseTest() {
        // given
        Member member = createMember();
        Post post = createPost(member);
        createRecommendPost(member, post);

        // when
        boolean isRecommend = actionStatusChecker.isRecommend(post.getId() + 1L, member.getId());

        // then
        assertThat(isRecommend).isFalse();
    }

    @Test
    @DisplayName("memberId 가 null 이면 false 를 반환한다.")
    void isRecommendPostMemberFalseTest() {
        // given

        // when
        boolean isRecommend = actionStatusChecker.isRecommend(null, null);

        // then
        assertThat(isRecommend).isFalse();
    }

    @Test
    @DisplayName("게시글 즐겨찾기 엔티티의 추천 여부 반환 시 True 를 반환한다.")
    void isFavoritePostIsTrueTest() {
        // given
        Member member = createMember();
        Post post = createPost(member);
        createFavoritePost(member, post);

        // when
        boolean isRecommend = actionStatusChecker.isFavorite(post.getId(), member.getId());

        // then
        assertThat(isRecommend).isTrue();
    }

    @Test
    @DisplayName("게시글 즐겨찾기 엔티티의 추천 여부 반환 시 False 를 반환한다.")
    void isFavoritePostIsFalseTest() {
        // given
        Member member = createMember();
        Post post = createPost(member);
        FavoritePost favoritePost = createFavoritePost(member, post);
        favoritePost.updateIsFavorite();

        // when
        boolean isRecommend = actionStatusChecker.isFavorite(post.getId(), member.getId());

        // then
        assertThat(isRecommend).isFalse();
    }


    @Test
    @DisplayName("게시글 즐겨찾기 엔티티 조회 후 null 이면 false 를 반환한다.")
    void isFavoritePostFalseTest() {
        // given
        Member member = createMember();
        Post post = createPost(member);
        createFavoritePost(member, post);

        // when
        boolean isRecommend = actionStatusChecker.isFavorite(post.getId() + 1L, member.getId());

        // then
        assertThat(isRecommend).isFalse();
    }

    @Test
    @DisplayName("memberId 가 null 이면 false 를 반환한다.")
    void isFavoritePostMemberFalseTest() {
        // given

        // when
        boolean isRecommend = actionStatusChecker.isFavorite(null, null);

        // then
        assertThat(isRecommend).isFalse();
    }

    private Member createMember() {
        Member member = Member.builder()
                .username("id")
                .password("pass")
                .nickname("nick")
                .build();
        return memberRepository.save(member);
    }

    private Post createPost(Member member) {
        Post post = Post.builder()
                .title("title")
                .content("content")
                .independentPostType(IndependentPostType.ETC)
                .member(member)
                .build();
        return postRepository.save(post);
    }

    private Comment createComment(Member member, Post post) {
        Comment comment = Comment.builder()
                .content("content")
                .member(member)
                .post(post)
                .build();
        return commentRepository.save(comment);
    }

    private RecommendComment createRecommendComment(Member member, Comment comment) {
        RecommendComment recommendComment = RecommendComment.builder()
                .isRecommend(true)
                .member(member)
                .comment(comment)
                .build();
        return recommendCommentRepository.save(recommendComment);
    }

    private RecommendPost createRecommendPost(Member member, Post post) {
        RecommendPost recommendPost = RecommendPost.builder()
                .isRecommend(true)
                .member(member)
                .post(post)
                .build();
        return recommendPostRepository.save(recommendPost);
    }

    private FavoritePost createFavoritePost(Member member, Post post) {
        FavoritePost favoritePost = FavoritePost.builder()
                .isFavorite(true)
                .post(post)
                .member(member)
                .build();
        return favoritePostRepository.save(favoritePost);
    }
}
