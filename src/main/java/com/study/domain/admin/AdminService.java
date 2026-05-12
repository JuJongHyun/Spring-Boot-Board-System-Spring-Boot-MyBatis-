package com.study.domain.admin;

import com.study.common.dto.SearchDTO;
import com.study.common.exception.BusinessException;
import com.study.common.exception.ErrorCode;
import com.study.common.paging.PagingResponse;
import com.study.domain.comment.CommentService;
import com.study.domain.admin.dto.DashboardStats;
import com.study.domain.member.dto.MemberResponse;
import com.study.domain.member.MemberRole;
import com.study.domain.member.MemberService;
import com.study.domain.post.PostResponse;
import com.study.domain.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;

    public DashboardStats getDashboardStats() {
        return DashboardStats.builder()
                .totalMembers(memberService.countActiveMembers())
                .todaySignups(memberService.countTodaySignups())
                .totalPosts(postService.countAllPosts())
                .totalComments(commentService.countAllComments())
                .build();
    }

    public PagingResponse<MemberResponse> findAllMembers(SearchDTO params) {
        return memberService.findAllMembersForAdmin(params);
    }

    @Transactional
    public void changeRole(Long memberId, MemberRole role) {
        memberService.changeRole(memberId, role);
    }

    @Transactional
    public void forceWithdraw(Long memberId) {
        if (memberService.findMemberById(memberId) == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        memberService.deleteMemberById(memberId);
    }

    public PagingResponse<PostResponse> findAllPosts(SearchDTO params) {
        return postService.findAllPost(params);
    }

    @Transactional
    public void deletePost(Long postId) {
        postService.deletePost(postId, null, true); // 관리자 삭제 — 소유권 검증 생략
    }
}
