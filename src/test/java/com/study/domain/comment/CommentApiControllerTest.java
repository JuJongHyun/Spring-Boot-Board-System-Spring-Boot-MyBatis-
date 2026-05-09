package com.study.domain.comment;

import com.study.domain.member.MemberResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommentApiControllerTest {

    @Test
    @DisplayName("[P0-1] 댓글 저장 시 URL의 postId가 params에 반드시 설정되어야 한다")
    void saveComment_postIdShouldBeSetFromPathVariable() {
        CommentService commentService = mock(CommentService.class);
        CommentApiController controller = new CommentApiController(commentService);
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("loginMember")).thenReturn(null);
        when(commentService.saveComment(any())).thenReturn(1L);
        when(commentService.findCommentById(1L)).thenReturn(mock(CommentResponse.class));

        CommentRequest params = new CommentRequest();
        Long postId = 42L;

        controller.saveComment(postId, params, session);

        ArgumentCaptor<CommentRequest> captor = ArgumentCaptor.forClass(CommentRequest.class);
        verify(commentService).saveComment(captor.capture());
        assertThat(captor.getValue().getPostId())
                .as("URL path variable postId가 params에 설정되어야 함")
                .isEqualTo(postId);
    }

    @Test
    @DisplayName("[P0-1] 로그인 사용자의 memberId와 writer가 params에 설정되어야 한다")
    void saveComment_memberInfoShouldBeSetFromSession() {
        CommentService commentService = mock(CommentService.class);
        CommentApiController controller = new CommentApiController(commentService);
        HttpSession session = mock(HttpSession.class);

        MemberResponse loginMember = mock(MemberResponse.class);
        when(loginMember.getId()).thenReturn(5L);
        when(loginMember.getName()).thenReturn("테스터");
        when(session.getAttribute("loginMember")).thenReturn(loginMember);
        when(commentService.saveComment(any())).thenReturn(1L);
        when(commentService.findCommentById(1L)).thenReturn(mock(CommentResponse.class));

        CommentRequest params = new CommentRequest();

        controller.saveComment(1L, params, session);

        ArgumentCaptor<CommentRequest> captor = ArgumentCaptor.forClass(CommentRequest.class);
        verify(commentService).saveComment(captor.capture());
        assertThat(captor.getValue().getMemberId()).isEqualTo(5L);
        assertThat(captor.getValue().getWriter()).isEqualTo("테스터");
    }
}
