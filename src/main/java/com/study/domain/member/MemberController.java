package com.study.domain.member;

import com.study.domain.member.dto.MemberResponse;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Hidden
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/login.do")
    public String openLoginPage() {
        return "member/login";
    }

    @GetMapping("/member/mypage.do")
    public String openMyPage(HttpSession session, Model model) {
        MemberResponse loginMember = (MemberResponse) session.getAttribute("loginMember");
        MemberResponse member = memberService.findMemberById(loginMember.getId());
        model.addAttribute("member", member);
        return "member/mypage";
    }

    // login.html 회원가입 팝업에서 사용하는 ID 중복 체크 (레거시 - /api/v1/members/check-id 와 동일)
    @GetMapping("/member-count")
    @ResponseBody
    public int countMemberByLoginId(@RequestParam final String loginId) {
        return memberService.countMemberByLoginId(loginId);
    }
}
