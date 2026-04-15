package com.study.domain.comment;

import com.study.common.paging.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class CommentApiController {

    private final CommentService commentService;

    // 신규 댓글 생성
    @PostMapping("/{postId}/comments")
    public CommentResponse saveComment(@PathVariable final Long postId, @RequestBody final CommentRequest params) {
        Long id = commentService.saveComment(params);
        return commentService.findCommentById(id);
    }

    // 댓글 리스트 조회
    @GetMapping("/{postId}/comments")
    public PagingResponse<CommentResponse> findAllComment(@PathVariable final Long postId, final CommentSearchDTO params) {
        return commentService.findAllComment(params);
    }

    // 댓글 샹세정보 조회
    @GetMapping("/{postId}/comments/{id}")
    public CommentResponse findCommentById(@PathVariable final Long postId, @PathVariable final Long id) {
        return commentService.findCommentById(id);
    }

    // 기존 댓글 수정
    @PatchMapping("/{postId}/comments/{id}")
    public CommentResponse updateComment(@PathVariable final Long postId, @PathVariable final Long id, @RequestBody final CommentRequest params) {
        commentService.updateComment(params);
        return commentService.findCommentById(id);
    }

    // 댓글 삭제
    @DeleteMapping("/{postId}/comments/{id}")
    public Long deleteComment(@PathVariable final Long postId, @PathVariable final Long id) {
        return commentService.deleteComment(id);
    }
}
