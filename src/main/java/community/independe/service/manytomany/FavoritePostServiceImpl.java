package community.independe.service.manytomany;

import community.independe.domain.manytomany.FavoritePost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.repository.manytomany.FavoritePostRepository;
import community.independe.repository.MemberRepository;
import community.independe.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FavoritePostServiceImpl implements FavoritePostService {

    private final FavoritePostRepository favoritePostRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public Long save(Long postId, Long memberId) {

        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("post not exist"));

        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("member not exist"));

        FavoritePost savedFavoritePost = favoritePostRepository.save(
                FavoritePost.builder()
                        .member(findMember)
                        .post(findPost)
                        .build()
        );

        return savedFavoritePost.getId();
    }

    @Override
    public FavoritePost findByPostIdAndMemberId(Long postId, Long memberId) {
        return favoritePostRepository.findByPostIdAndMemberId(postId, memberId);
    }

    @Override
    @Transactional
    public void updateIsFavorite(FavoritePost favoritePost, Boolean isFavorite) {
        favoritePost.updateFavorite(isFavorite);
    }
}