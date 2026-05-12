package com.study.domain.comment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 댓글 응답
@Getter
public class CommentResponse {

    private Long id;                        // 댓글 번호 (PK)
    private Long postId;                    // 게시글 번호 (FK)
    private Long parentId;                  // 부모 댓글 번호 (대댓글인 경우)
    private Long memberId;                  // 작성자 회원 번호 (FK)
    private String content;                 // 내용
    private String writer;                  // 작성자
    private Boolean deleteYn;               // 삭제 여부
    private LocalDateTime createdDate;      // 생성일시
    private LocalDateTime modifiedDate;     // 최종 수정일시

    @Setter
    private boolean isOwner;               // 수정/삭제 권한 여부 (작성자 또는 ADMIN)

    @Setter
    private List<CommentResponse> replies = new ArrayList<>();  // 대댓글 목록
}
