package community.independe.service;

import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.repository.MemberRepository;
import community.independe.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Override
    public Post findById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Id not exist"));
    }

    // 자취 게시글 생성
    @Transactional
    @Override
    public Long createIndependentPost(Long memberId, String title, String content, IndependentPostType independentPostType) {

        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Id not exist"));

        Post post = Post.builder()
                .title(title)
                .content(content)
                .member(findMember)
                .independentPostType(independentPostType)
                .build();

        postRepository.save(post);
        return post.getId();
    }

    // 지역 게시글 생성
    @Override
    @Transactional
    public Long createRegionPost(Long memberId, String title, String content, RegionType regionType, RegionPostType regionPostType) {

        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Id not exist"));

        Post post = Post.builder()
                .title(title)
                .content(content)
                .member(findMember)
                .regionType(regionType)
                .regionPostType(regionPostType)
                .build();

        postRepository.save(post);
        return post.getId();
    }

    @Override
    public Page<Post> findAllIndependentPostsByTypeWithMember(IndependentPostType independentPostType, String condition, String keyword, Pageable pageable) {
//        return postRepository.findAllIndependentPostsByTypeWithMember(independentPostType, pageable);
        return postRepository.findAllIndependentPostsByTypeWithMemberDynamic(independentPostType, condition, keyword, pageable);
    }

    @Override
    public Page<Post> findAllRegionPostsByTypesWithMember(RegionType regionType, RegionPostType regionPostType, String condition, String keyword, Pageable pageable) {
//        return postRepository.findAllRegionPostsByTypesWithMember(regionType, regionPostType, pageable);
        return postRepository.findAllRegionPostsByTypesWithMemberDynamic(regionType, regionPostType, condition, keyword, pageable);
    }

    @Override
    @Transactional
    public void increaseViews(Long postId) {
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Id not exist"));

        findPost.increaseViews(findPost.getViews() + 1);
    }
}
