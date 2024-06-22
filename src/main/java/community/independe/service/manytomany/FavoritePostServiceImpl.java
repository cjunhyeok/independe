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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

        FavoritePost findFavoritePost = favoritePostRepository.findByPostIdAndMemberId(postId, memberId);

        if (findFavoritePost == null) {
            FavoritePost savedFavoritePost = favoritePostRepository.save(
                    FavoritePost.builder()
                            .member(findMember)
                            .post(findPost)
                            .isFavorite(true)
                            .build()
            );

            return savedFavoritePost.getId();
        } else {
            findFavoritePost.updateIsFavorite();

            return findFavoritePost.getId();
        }
    }

    @Override
    public List<GetFavoritePostServiceDto> findFavoritePostByMemberId(Long memberId, int page, int size) {

        PageRequest request = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        Page<FavoritePost> findFavoritePostPage = favoritePostRepository.findByMemberId(memberId, request);
        List<FavoritePost> findFavoritePosts = findFavoritePostPage.getContent();
        long totalCount = findFavoritePostPage.getTotalElements();

        List<GetFavoritePostServiceDto> serviceDto = findFavoritePosts.stream()
                .map(fp -> {
                    Post post = fp.getPost();
                    Member member = fp.getMember();
                    return GetFavoritePostServiceDto.builder()
                            .postId(post.getId())
                            .memberId(member.getId())
                            .title(post.getTitle())
                            .independentPostType(post.getIndependentPostType())
                            .regionType(post.getRegionType())
                            .regionPostType(post.getRegionPostType())
                            .nickname(member.getNickname())
                            .createdDate(fp.getCreatedDate())
                            .totalCount(totalCount)
                            .build();
                })
                .collect(Collectors.toList());

        return serviceDto;
    }
}
