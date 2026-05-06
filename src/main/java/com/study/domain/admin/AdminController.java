package com.study.domain.admin;

import com.study.common.dto.SearchDTO;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Hidden
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/index.do")
    public String index(
            @RequestParam(defaultValue = "dashboard") String tab,
            @RequestParam(defaultValue = "") String mKeyword,
            @RequestParam(defaultValue = "1") int mPage,
            @RequestParam(defaultValue = "") String pKeyword,
            @RequestParam(defaultValue = "") String pSearchType,
            @RequestParam(defaultValue = "1") int pPage,
            Model model) {

        model.addAttribute("stats", adminService.getDashboardStats());
        model.addAttribute("activeTab", tab);

        SearchDTO memberParams = new SearchDTO();
        memberParams.setKeyword(mKeyword);
        memberParams.setPage(mPage);
        model.addAttribute("members", adminService.findAllMembers(memberParams));
        model.addAttribute("mKeyword", mKeyword);
        model.addAttribute("mPage", mPage);

        SearchDTO postParams = new SearchDTO();
        postParams.setKeyword(pKeyword);
        postParams.setSearchType(pSearchType);
        postParams.setPage(pPage);
        model.addAttribute("posts", adminService.findAllPosts(postParams));
        model.addAttribute("pKeyword", pKeyword);
        model.addAttribute("pSearchType", pSearchType);
        model.addAttribute("pPage", pPage);

        return "admin/index";
    }
}
