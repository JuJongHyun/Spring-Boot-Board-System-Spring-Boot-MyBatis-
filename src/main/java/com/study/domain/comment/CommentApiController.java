package com.study.domain.comment;

import com.study.common.paging.PagingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment", description = "댓글 API (UI 연동용)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class CommentApiController {

    private final CommentService commentService;

    @Operation(summary = "댓글 생성")
    @PostMapping("/{postId}/comments")
    public CommentResponse saveComment(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @RequestBody CommentRequest params) {
        Long id = commentService.saveComment(params);
        return commentService.findCommentById(id);
    }

    @Operation(summary = "댓글 목록 조회")
    @GetMapping("/{postId}/comments")
    public PagingResponse<CommentResponse> findAllComment(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            CommentSearchDTO params) {
        return commentService.findAllComment(params);
    }

    @Operation(summary = "댓글 상세 조회")
    @GetMapping("/{postId}/comments/{id}")
    public CommentResponse findCommentById(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @Parameter(description = "댓글 ID") @PathVariable Long id) {
        return commentService.findCommentById(id);
    }

    @Operation(summary = "댓글 수정")
    @PatchMapping("/{postId}/comments/{id}")
    public CommentResponse updateComment(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @Parameter(description = "댓글 ID") @PathVariable Long id,
            @RequestBody CommentRequest params) {
        commentService.updateComment(params);
        return commentService.findCommentById(id);
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/{postId}/comments/{id}")
    public Long deleteComment(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @Parameter(description = "댓글 ID") @PathVariable Long id) {
        return commentService.deleteComment(id);
    }
}
