package community.independe.service.manytomany;

import community.independe.domain.manytomany.FavoritePost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.exception.notfound.MemberNotFountException;
import community.independe.exception.notfound.PostNotFountException;
import community.independe.repository.manytomany.FavoritePostRepository;
import community.independe.repository.MemberRepository;
import community.independe.repository.post.PostRepository;
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
                .orElseThrow(() -> new PostNotFountException("Post Not Exist"));

        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFountException("Member Not Exist"));

        FavoritePost savedFavoritePost = favoritePostRepository.save(
                FavoritePost.builder()
                        .member(findMember)
                        .post(findPost)
                        .isFavorite(true)
                        .build()
        );

        return savedFavoritePost.getId();
    }

    @Override
    @Transactional
    public void updateIsFavorite(FavoritePost favoritePost, Boolean isFavorite) {
        FavoritePost findFavoritePost = favoritePostRepository.findById(favoritePost.getId()).orElseThrow(
                () -> new IllegalArgumentException("FavoritePost not exist")
        );
        findFavoritePost.updateIsFavorite(isFavorite);
    }

    @Override
    public FavoritePost findByPostIdAndMemberId(Long postId, Long memberId) {
        return favoritePostRepository.findByPostIdAndMemberId(postId, memberId);
    }

    @Override
    public FavoritePost findByPostIdAndMemberIdAndIsRecommend(Long postId, Long memberId) {
        return favoritePostRepository.findByPostIdAndMemberIdAndIsRecommend(postId, memberId);
    }
}
