package com.study.domain.comment;

import com.study.common.exception.BusinessException;
import com.study.common.exception.ErrorCode;
import com.study.domain.member.MemberService;
import com.study.domain.notification.NotificationService;
import com.study.domain.post.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CommentOwnershipTest {

    private CommentService buildService(CommentMapper commentMapper) {
        return new CommentService(
                commentMapper,
                mock(PostService.class),
                mock(MemberService.class),
                mock(NotificationService.class)
        );
    }

    @Test
    @DisplayName("[C1] 타인이 댓글을 삭제하려 하면 FORBIDDEN 예외가 발생해야 한다")
    void deleteComment_byNonOwner_shouldThrowForbidden() {
        CommentMapper commentMapper = mock(CommentMapper.class);
        CommentService commentService = buildService(commentMapper);

        CommentResponse comment = mock(CommentResponse.class);
        when(comment.getMemberId()).thenReturn(1L);
        when(commentMapper.findById(5L)).thenReturn(comment);

        assertThatThrownBy(() -> commentService.deleteComment(5L, 2L, false))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FORBIDDEN);
    }

    @Test
    @DisplayName("[C1] 댓글 소유자는 자신의 댓글을 삭제할 수 있어야 한다")
    void deleteComment_byOwner_shouldSucceed() {
        CommentMapper commentMapper = mock(CommentMapper.class);
        CommentService commentService = buildService(commentMapper);

        CommentResponse comment = mock(CommentResponse.class);
        when(comment.getMemberId()).thenReturn(1L);
        when(commentMapper.findById(5L)).thenReturn(comment);

        commentService.deleteComment(5L, 1L, false);

        verify(commentMapper).deleteById(5L);
    }

    @Test
    @DisplayName("[C1] 관리자는 타인의 댓글도 삭제할 수 있어야 한다")
    void deleteComment_byAdmin_shouldSucceed() {
        CommentMapper commentMapper = mock(CommentMapper.class);
        CommentService commentService = buildService(commentMapper);

        CommentResponse comment = mock(CommentResponse.class);
        when(comment.getMemberId()).thenReturn(1L);
        when(commentMapper.findById(5L)).thenReturn(comment);

        commentService.deleteComment(5L, 99L, true); // isAdmin=true

        verify(commentMapper).deleteById(5L);
    }

    @Test
    @DisplayName("[C1] 타인이 댓글을 수정하려 하면 FORBIDDEN 예외가 발생해야 한다")
    void updateComment_byNonOwner_shouldThrowForbidden() {
        CommentMapper commentMapper = mock(CommentMapper.class);
        CommentService commentService = buildService(commentMapper);

        CommentResponse comment = mock(CommentResponse.class);
        when(comment.getMemberId()).thenReturn(1L);
        when(commentMapper.findById(5L)).thenReturn(comment);

        CommentRequest params = new CommentRequest();
        params.setId(5L);
        params.setContent("수정 내용");

        assertThatThrownBy(() -> commentService.updateComment(params, 2L, false))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FORBIDDEN);
    }
}
