package com.study.domain.admin;

import com.study.common.dto.SearchDTO;
import com.study.common.paging.PagingResponse;
import com.study.domain.member.MemberResponse;
import com.study.domain.post.PostResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Hidden
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard.do")
    public String dashboard(Model model) {
        model.addAttribute("stats", adminService.getDashboardStats());
        return "admin/dashboard";
    }

    @GetMapping("/members.do")
    public String members(SearchDTO params, Model model) {
        PagingResponse<MemberResponse> response = adminService.findAllMembers(params);
        model.addAttribute("response", response);
        return "admin/members";
    }

    @GetMapping("/posts.do")
    public String posts(SearchDTO params, Model model) {
        PagingResponse<PostResponse> response = adminService.findAllPosts(params);
        model.addAttribute("response", response);
        return "admin/posts";
    }
}
