package com.study.domain.post;

import com.study.domain.member.MemberResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PostControllerTest {

    @Test
    @DisplayName("[P0-2] 게시글 저장(MVC) 시 로그인 사용자 이름이 writer로 설정되어야 한다")
    void savePost_writerShouldBeSetFromLoginMember() {
        PostService postService = mock(PostService.class);
        PostController controller = new PostController(postService);
        HttpSession session = mock(HttpSession.class);
        Model model = mock(Model.class);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        MemberResponse loginMember = mock(MemberResponse.class);
        when(loginMember.getId()).thenReturn(1L);
        when(loginMember.getName()).thenReturn("홍길동");
        when(session.getAttribute("loginMember")).thenReturn(loginMember);

        PostRequest params = new PostRequest();
        controller.savePost(params, model, session);

        assertThat(params.getWriter())
                .as("로그인 사용자 이름이 writer로 설정되어야 함")
                .isEqualTo("홍길동");
    }

    @Test
    @DisplayName("[P1-1] 삭제된 게시글 조회 시 목록으로 리다이렉트되어야 한다")
    void openPostView_withDeletedPost_shouldRedirectToList() {
        PostService postService = mock(PostService.class);
        PostController controller = new PostController(postService);
        Model model = mock(Model.class);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        PostResponse deletedPost = mock(PostResponse.class);
        when(deletedPost.getDeleteYn()).thenReturn(Boolean.TRUE);
        when(postService.findPostById(1L)).thenReturn(deletedPost);

        String viewName = controller.openPostView(1L, model);

        assertThat(viewName)
                .as("삭제된 게시글은 messageRedirect로 이동해야 함")
                .isEqualTo("common/messageRedirect");
        verify(postService, never()).increaseViewCount(anyLong());
    }

    @Test
    @DisplayName("[P1-1] null 게시글 조회 시 목록으로 리다이렉트되어야 한다")
    void openPostView_withNullPost_shouldRedirectToList() {
        PostService postService = mock(PostService.class);
        PostController controller = new PostController(postService);
        Model model = mock(Model.class);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        when(postService.findPostById(99L)).thenReturn(null);

        String viewName = controller.openPostView(99L, model);

        assertThat(viewName)
                .as("존재하지 않는 게시글은 messageRedirect로 이동해야 함")
                .isEqualTo("common/messageRedirect");
        verify(postService, never()).increaseViewCount(anyLong());
    }

    @Test
    @DisplayName("[QA-N1] 삭제된 게시글의 수정 페이지 접근 시 write 페이지 대신 리다이렉트되어야 한다")
    void openPostWrite_withDeletedPost_shouldRedirectToList() {
        PostService postService = mock(PostService.class);
        PostController controller = new PostController(postService);
        Model model = mock(Model.class);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        PostResponse deletedPost = mock(PostResponse.class);
        when(deletedPost.getDeleteYn()).thenReturn(Boolean.TRUE);
        when(postService.findPostById(1L)).thenReturn(deletedPost);

        String viewName = controller.openPostWrite(1L, model);

        assertThat(viewName)
                .as("삭제된 게시글 수정 페이지 접근 시 messageRedirect로 이동해야 함")
                .isEqualTo("common/messageRedirect");
    }

    @Test
    @DisplayName("[QA-N1] 존재하지 않는 게시글의 수정 페이지 접근 시 리다이렉트되어야 한다")
    void openPostWrite_withNullPost_shouldRedirectToList() {
        PostService postService = mock(PostService.class);
        PostController controller = new PostController(postService);
        Model model = mock(Model.class);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        when(postService.findPostById(99L)).thenReturn(null);

        String viewName = controller.openPostWrite(99L, model);

        assertThat(viewName)
                .as("존재하지 않는 게시글 수정 페이지 접근 시 messageRedirect로 이동해야 함")
                .isEqualTo("common/messageRedirect");
    }

    @Test
    @DisplayName("[P1-1] 정상 게시글 조회 시 조회수가 증가하고 view 페이지를 반환해야 한다")
    void openPostView_withValidPost_shouldIncreaseViewCountAndReturnView() {
        PostService postService = mock(PostService.class);
        PostController controller = new PostController(postService);
        Model model = mock(Model.class);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        PostResponse validPost = mock(PostResponse.class);
        when(validPost.getDeleteYn()).thenReturn(Boolean.FALSE);
        when(postService.findPostById(1L)).thenReturn(validPost);

        String viewName = controller.openPostView(1L, model);

        assertThat(viewName).isEqualTo("post/view");
        verify(postService).increaseViewCount(1L);
    }
}
