package com.study.domain.post;

import com.study.common.dto.ApiResponse;
import com.study.common.dto.SearchDTO;
import com.study.common.exception.BusinessException;
import com.study.common.exception.ErrorCode;
import com.study.common.paging.PagingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Post", description = "게시글 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostApiController {

    private final PostService postService;

    @Operation(summary = "게시글 목록 조회", description = "검색 조건과 페이징을 적용한 게시글 목록을 반환합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<PagingResponse<PostResponse>>> findAllPost(
            @ModelAttribute SearchDTO params) {
        return ResponseEntity.ok(ApiResponse.ok(postService.findAllPost(params)));
    }

    @Operation(summary = "게시글 상세 조회", description = "게시글 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> findPostById(
            @Parameter(description = "게시글 ID") @PathVariable Long id) {
        PostResponse post = postService.findPostById(id);
        if (post == null || Boolean.TRUE.equals(post.getDeleteYn())) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }
        return ResponseEntity.ok(ApiResponse.ok(post));
    }

    @Operation(summary = "게시글 생성", description = "새 게시글을 생성합니다. 파일 첨부 시 multipart/form-data 사용.")
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> savePost(@ModelAttribute PostRequest params) {
        Long id = postService.savePost(params);
        return ResponseEntity.status(201).body(ApiResponse.created(id));
    }

    @Operation(summary = "게시글 수정", description = "기존 게시글을 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Long>> updatePost(
            @Parameter(description = "게시글 ID") @PathVariable Long id,
            @ModelAttribute PostRequest params) {
        params.setId(id);
        postService.updatePost(params);
        return ResponseEntity.ok(ApiResponse.ok("게시글이 수정되었습니다.", id));
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 소프트 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Long>> deletePost(
            @Parameter(description = "게시글 ID") @PathVariable Long id) {
        PostResponse post = postService.findPostById(id);
        if (post == null || Boolean.TRUE.equals(post.getDeleteYn())) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }
        postService.deletePost(id);
        return ResponseEntity.ok(ApiResponse.ok("게시글이 삭제되었습니다.", id));
    }
}
