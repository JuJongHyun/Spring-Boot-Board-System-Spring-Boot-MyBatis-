package com.study.domain.admin;

import com.study.common.dto.SearchDTO;
import com.study.common.exception.BusinessException;
import com.study.common.exception.ErrorCode;
import com.study.common.paging.Pagination;
import com.study.common.paging.PagingResponse;
import com.study.domain.comment.CommentMapper;
import com.study.domain.member.MemberMapper;
import com.study.domain.member.MemberResponse;
import com.study.domain.member.MemberRole;
import com.study.domain.post.PostMapper;
import com.study.domain.post.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberMapper memberMapper;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    public DashboardStats getDashboardStats() {
        return DashboardStats.builder()
                .totalMembers(memberMapper.countActive())
                .todaySignups(memberMapper.countToday())
                .totalPosts(postMapper.countAll())
                .totalComments(commentMapper.countAll())
                .build();
    }

    public PagingResponse<MemberResponse> findAllMembers(SearchDTO params) {
        int count = memberMapper.countForAdmin(params);
        if (count < 1) {
            return new PagingResponse<>(Collections.emptyList(), null);
        }
        Pagination pagination = new Pagination(count, params);
        params.setPagination(pagination);
        List<MemberResponse> list = memberMapper.findAllForAdmin(params);
        return new PagingResponse<>(list, pagination);
    }

    @Transactional
    public void changeRole(Long memberId, MemberRole role) {
        if (memberMapper.findById(memberId) == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        memberMapper.updateRole(memberId, role);
    }

    @Transactional
    public void forceWithdraw(Long memberId) {
        if (memberMapper.findById(memberId) == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        memberMapper.deleteById(memberId);
    }

    public PagingResponse<PostResponse> findAllPosts(SearchDTO params) {
        int count = postMapper.count(params);
        if (count < 1) {
            return new PagingResponse<>(Collections.emptyList(), null);
        }
        Pagination pagination = new Pagination(count, params);
        params.setPagination(pagination);
        List<PostResponse> list = postMapper.findAll(params);
        return new PagingResponse<>(list, pagination);
    }

    @Transactional
    public void deletePost(Long postId) {
        if (postMapper.findById(postId) == null) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }
        postMapper.deleteById(postId);
    }
}
