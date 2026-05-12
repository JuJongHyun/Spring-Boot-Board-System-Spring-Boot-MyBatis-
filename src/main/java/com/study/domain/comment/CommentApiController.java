package com.study.domain.comment;

import com.study.common.dto.ApiResponse;
import com.study.common.exception.BusinessException;
import com.study.common.exception.ErrorCode;
import com.study.common.paging.PagingResponse;
import com.study.domain.member.MemberRole;
import com.study.domain.member.dto.MemberResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment", description = "댓글 API (UI 연동용)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class CommentApiController {

    private final CommentService commentService;

    @Operation(summary = "댓글 생성")
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> saveComment(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @RequestBody CommentRequest params,
            HttpSession session) {
        params.setPostId(postId);
        MemberResponse loginMember = (MemberResponse) session.getAttribute("loginMember");
        if (loginMember != null) {
            params.setMemberId(loginMember.getId());
            params.setWriter(loginMember.getName());
        }
        Long id = commentService.saveComment(params);
        return ResponseEntity.status(201).body(ApiResponse.created(commentService.findCommentById(id)));
    }

    @Operation(summary = "댓글 목록 조회")
    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<PagingResponse<CommentResponse>>> findAllComment(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            CommentSearchDTO params) {
        return ResponseEntity.ok(ApiResponse.ok(commentService.findAllComment(params)));
    }

    @Operation(summary = "댓글 상세 조회")
    @GetMapping("/{postId}/comments/{id}")
    public ResponseEntity<ApiResponse<CommentResponse>> findCommentById(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @Parameter(description = "댓글 ID") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(commentService.findCommentById(id)));
    }

    @Operation(summary = "댓글 수정")
    @PatchMapping("/{postId}/comments/{id}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @Parameter(description = "댓글 ID") @PathVariable Long id,
            @RequestBody CommentRequest params,
            HttpSession session) {
        MemberResponse loginMember = getLoginMember(session);
        params.setId(id);
        commentService.updateComment(params, loginMember.getId(), isAdmin(loginMember));
        return ResponseEntity.ok(ApiResponse.ok(commentService.findCommentById(id)));
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/{postId}/comments/{id}")
    public ResponseEntity<ApiResponse<Long>> deleteComment(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @Parameter(description = "댓글 ID") @PathVariable Long id,
            HttpSession session) {
        MemberResponse loginMember = getLoginMember(session);
        return ResponseEntity.ok(ApiResponse.ok("댓글이 삭제되었습니다.", commentService.deleteComment(id, loginMember.getId(), isAdmin(loginMember))));
    }

    private MemberResponse getLoginMember(HttpSession session) {
        MemberResponse member = (MemberResponse) session.getAttribute("loginMember");
        if (member == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return member;
    }

    private boolean isAdmin(MemberResponse member) {
        return MemberRole.ADMIN == member.getRole();
    }
}
