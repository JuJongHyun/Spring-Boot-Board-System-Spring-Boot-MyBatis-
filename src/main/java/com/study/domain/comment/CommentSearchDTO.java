package com.study.domain.comment;

import com.study.common.dto.SearchDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentSearchDTO extends SearchDTO {

    private Long postId;    // 게시판 번호 (FK)
}
