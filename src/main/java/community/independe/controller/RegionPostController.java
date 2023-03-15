package community.independe.controller;

import community.independe.controller.form.RegionPostForm;
import community.independe.domain.member.Member;
import community.independe.domain.post.RegionPost;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.service.MemberService;
import community.independe.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RegionPostController {

    private final MemberService memberService;
    private final PostService postService;

    //== 지역 게시글 작성==//
    @GetMapping("/posts/region/new")
    public String createRegionPostForm(Model model) {

        model.addAttribute("postForm", new RegionPostForm());
        model.addAttribute("regionTypes", RegionType.values());
        model.addAttribute("regionPostTypes", RegionPostType.values());
        return "posts/createRegionPostForm";
    }

    @PostMapping("posts/region/new")
    public String createRegionPost(@Valid RegionPostForm form,
                                   BindingResult result,
                                   @AuthenticationPrincipal Member member) {
        if (result.hasErrors()) {
            return "posts/createIndependentPostForm";
        }

        if (member == null) {

            // for test member (before add login in security)
            member = memberService.findById(1L);
        }

        postService.createRegionPost(member.getId(), form.getTitle(), form.getContent(), form.getRegionType(), form.getRegionPostType());
        return "redirect:/";
    }

    @GetMapping("/posts/region/{regionType}/{postType}")
    public String regionPosts(@PathVariable(value = "regionType") RegionType regionType,
                              @PathVariable(value = "postType") RegionPostType postType,
                              @PageableDefault(size = 3) Pageable pageable,
                              Model model) {

        Page<RegionPost> allRegionPosts = postService.findAllRegionPostsByTypes(regionType, postType, pageable);
        List<RegionPost> content = allRegionPosts.getContent();
        long totalElements = allRegionPosts.getTotalElements();

        model.addAttribute("regionPost", content);
        model.addAttribute("totalCount", totalElements);

        return "posts/regionPosts";
    }
}
