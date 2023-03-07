package community.independe.controller;

import community.independe.controller.form.IndependentPostForm;
import community.independe.domain.member.Member;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.service.MemberService;
import community.independe.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final MemberService memberService;
    private final PostService postService;

    @GetMapping("/posts/independent/new")
    public String createPostForm(Model model) {

        model.addAttribute("postForm", new IndependentPostForm());
        model.addAttribute("independentTypes", IndependentPostType.values());
        return "posts/createIndependentPostForm";
    }

    @PostMapping("/posts/independent/new")
    public String createPost(@Valid IndependentPostForm form,
                             BindingResult result,
                             @AuthenticationPrincipal Member member) {

        log.info("in createPost");

        if (result.hasErrors()) {
            return "posts/createIndependentPostForm";
        }

        if (member == null) {

            // for test member (before add login in security)
            member = memberService.findById(1L);
        }

        postService.createIndependentPost(member.getId(), form.getTitle(), form.getContent(), form.getIndependentPostType());
        return "redirect:/";
    }
}
