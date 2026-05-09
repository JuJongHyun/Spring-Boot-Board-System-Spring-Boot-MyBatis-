package com.study.domain.post;

import com.study.domain.member.MemberResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PostApiControllerWriterTest {

    @Test
    @DisplayName("[P0-2] 게시글 저장(API) 시 로그인 사용자 이름이 writer로 설정되어야 한다")
    void savePost_writerShouldBeSetFromLoginMember() {
        PostService postService = mock(PostService.class);
        PostApiController controller = new PostApiController(postService);
        HttpSession session = mock(HttpSession.class);

        MemberResponse loginMember = mock(MemberResponse.class);
        when(loginMember.getId()).thenReturn(1L);
        when(loginMember.getName()).thenReturn("홍길동");
        when(session.getAttribute("loginMember")).thenReturn(loginMember);
        when(postService.savePost(any())).thenReturn(1L);

        PostRequest params = new PostRequest();

        controller.savePost(params, session);

        ArgumentCaptor<PostRequest> captor = ArgumentCaptor.forClass(PostRequest.class);
        verify(postService).savePost(captor.capture());
        assertThat(captor.getValue().getWriter())
                .as("로그인 사용자 이름이 writer로 설정되어야 함")
                .isEqualTo("홍길동");
        assertThat(captor.getValue().getMemberId())
                .as("로그인 사용자 ID가 memberId로 설정되어야 함")
                .isEqualTo(1L);
    }
}
