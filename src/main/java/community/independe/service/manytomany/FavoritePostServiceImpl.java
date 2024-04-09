package community.independe.service.manytomany;

import community.independe.domain.manytomany.FavoritePost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.manytomany.FavoritePostRepository;
import community.independe.repository.MemberRepository;
import community.independe.repository.post.PostRepository;
import community.independe.service.manytomany.dtos.GetFavoritePostServiceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

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
                () -> new CustomException(ErrorCode.FAVORITE_POST_NOT_FOUND)
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

    @Override
    public List<GetFavoritePostServiceDto> findFavoritePostByMemberId(Long memberId) {
        List<FavoritePost> findFavoritePosts = favoritePostRepository.findByMemberId(memberId);

        List<GetFavoritePostServiceDto> serviceDto = findFavoritePosts.stream()
                .map(fp -> {
                    Post post = fp.getPost();
                    Member member = fp.getMember();
                    return GetFavoritePostServiceDto.builder()
                            .title(post.getTitle())
                            .independentPostType(post.getIndependentPostType())
                            .regionType(post.getRegionType())
                            .regionPostType(post.getRegionPostType())
                            .nickname(member.getNickname())
                            .createdDate(fp.getCreatedDate())
                            .build();
                })
                .collect(Collectors.toList());

        return serviceDto;
    }
}
