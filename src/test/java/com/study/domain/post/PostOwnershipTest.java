package com.study.domain.post;

import com.study.common.exception.BusinessException;
import com.study.common.exception.ErrorCode;
import com.study.domain.member.MemberRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PostOwnershipTest {

    @Test
    @DisplayName("[C1] 타인이 게시글을 삭제하려 하면 FORBIDDEN 예외가 발생해야 한다")
    void deletePost_byNonOwner_shouldThrowForbidden() {
        PostMapper postMapper = mock(PostMapper.class);
        PostService postService = new PostService(postMapper, mock(com.study.domain.file.FileService.class), mock(com.study.common.file.FileUtils.class));

        PostResponse post = mock(PostResponse.class);
        when(post.getDeleteYn()).thenReturn(false);
        when(post.getMemberId()).thenReturn(1L);
        when(postMapper.findById(10L)).thenReturn(post);

        assertThatThrownBy(() -> postService.deletePost(10L, 2L, false))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FORBIDDEN);
    }

    @Test
    @DisplayName("[C1] 게시글 소유자는 자신의 게시글을 삭제할 수 있어야 한다")
    void deletePost_byOwner_shouldSucceed() {
        PostMapper postMapper = mock(PostMapper.class);
        com.study.domain.file.FileService fileService = mock(com.study.domain.file.FileService.class);
        com.study.common.file.FileUtils fileUtils = mock(com.study.common.file.FileUtils.class);
        PostService postService = new PostService(postMapper, fileService, fileUtils);

        PostResponse post = mock(PostResponse.class);
        when(post.getDeleteYn()).thenReturn(false);
        when(post.getMemberId()).thenReturn(1L);
        when(postMapper.findById(10L)).thenReturn(post);
        when(fileService.findAllFileByPostId(10L)).thenReturn(java.util.Collections.emptyList());

        postService.deletePost(10L, 1L, false);

        verify(postMapper).deleteById(10L);
    }

    @Test
    @DisplayName("[C1] 관리자는 타인의 게시글도 삭제할 수 있어야 한다")
    void deletePost_byAdmin_shouldSucceed() {
        PostMapper postMapper = mock(PostMapper.class);
        com.study.domain.file.FileService fileService = mock(com.study.domain.file.FileService.class);
        com.study.common.file.FileUtils fileUtils = mock(com.study.common.file.FileUtils.class);
        PostService postService = new PostService(postMapper, fileService, fileUtils);

        PostResponse post = mock(PostResponse.class);
        when(post.getDeleteYn()).thenReturn(false);
        when(post.getMemberId()).thenReturn(1L);
        when(postMapper.findById(10L)).thenReturn(post);
        when(fileService.findAllFileByPostId(10L)).thenReturn(java.util.Collections.emptyList());

        postService.deletePost(10L, 99L, true); // isAdmin=true

        verify(postMapper).deleteById(10L);
    }

    @Test
    @DisplayName("[C1] 타인이 게시글을 수정하려 하면 FORBIDDEN 예외가 발생해야 한다")
    void updatePost_byNonOwner_shouldThrowForbidden() {
        PostMapper postMapper = mock(PostMapper.class);
        PostService postService = new PostService(postMapper, mock(com.study.domain.file.FileService.class), mock(com.study.common.file.FileUtils.class));

        PostResponse post = mock(PostResponse.class);
        when(post.getDeleteYn()).thenReturn(false);
        when(post.getMemberId()).thenReturn(1L);
        when(postMapper.findById(10L)).thenReturn(post);

        PostRequest params = new PostRequest();
        params.setId(10L);

        assertThatThrownBy(() -> postService.updatePost(params, 2L, false))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FORBIDDEN);
    }
}
