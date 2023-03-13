package community.independe.controller;

import community.independe.controller.form.MemberForm;
import community.independe.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createMemberForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String createMember(@Valid MemberForm memberForm, BindingResult result) {
        if (result.hasErrors()) {
            return "members/createMemberForm";
        }

        memberService.join(memberForm.getUsername(),
                memberForm.getUserPassword(),
                memberForm.getNickname(),
                memberForm.getNickname(),
                memberForm.getNumber(),
                memberForm.getCity(),
                memberForm.getStreet(),
                memberForm.getZipcode());

        return "redirect:/";
    }
}