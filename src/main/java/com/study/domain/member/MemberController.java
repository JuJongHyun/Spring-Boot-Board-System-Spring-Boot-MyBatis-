package com.study.domain.member;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Hidden
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 로그인 페이지
    @GetMapping("/login.do")
    public String openLoginPage() {
        return "member/login";
    }

    // 회원 정보 저장 (회원가입)
    @PostMapping("/members")
    @ResponseBody
    public Long saveMember(@RequestBody final MemberRequest params) {
        return memberService.saveMember(params);
    }

    // 회원 상세정보 조회
    @GetMapping("/members/{loginId}")
    @ResponseBody
    public MemberResponse findMemberByLoginId(@PathVariable final String loginId) {
        return memberService.findMemberByLoginId(loginId);
    }

    // 회원 정보 삭제 (회원 탈퇴)
    @DeleteMapping("/members/{id}")
    @ResponseBody
    public Long deleteMember(@PathVariable final Long id) {
        return memberService.deleteMemberById(id);
    }

    // 회원 수 카운팅 (ID 중복 체크)
    @GetMapping("/member-count")
    @ResponseBody
    public int countMemberByLoginId(@RequestParam final String loginId) {
        return memberService.countMemberByLoginId(loginId);
    }

    // 로그인
    @PostMapping("/login")
    @ResponseBody
    public MemberResponse login (HttpServletRequest request) {

        // 1. 회원 정보 조회
        String loginId = request.getParameter("loginId");
        String password = request.getParameter("password");

        MemberResponse member = memberService.login(loginId, password);

        // 2. 세션에 회원 정보 저장 & 세션 유지 시간 설정
        if (member != null) {
            HttpSession session = request.getSession();
            session.setAttribute("loginMember", member);
            session.setMaxInactiveInterval(60 * 30);
        }
        return member;
    }

    // 로그아웃
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login.do";
    }

}
