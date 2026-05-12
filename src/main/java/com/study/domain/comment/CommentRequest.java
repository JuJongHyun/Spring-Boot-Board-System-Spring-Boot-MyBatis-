package com.study.domain.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 댓글 요청
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentRequest {

    private Long id;            // 댓글 번호 (PK)
    private Long postId;        // 게시글 번호 (FK)
    private Long parentId;      // 부모 댓글 번호 (대댓글인 경우)
    private String content;     // 내용
    private String writer;      // 작성자

    @JsonIgnore
    private Long memberId;      // 작성자 회원 번호 (세션에서 주입, 클라이언트 전송 불가)
}
