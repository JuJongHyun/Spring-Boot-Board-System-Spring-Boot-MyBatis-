package com.study.interceptor;

import com.study.domain.member.dto.MemberResponse;
import com.study.global.interceptor.LoginCheckInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginCheckInterceptorTest {

    @InjectMocks LoginCheckInterceptor interceptor;

    @Test
    void 로그인_세션_있으면_통과() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        MemberResponse member = mock(MemberResponse.class);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("loginMember")).thenReturn(member);
        when(member.getDeleteYn()).thenReturn(false);

        boolean result = interceptor.preHandle(request, response, null);

        assertThat(result).isTrue();
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    void 세션에_회원정보_없으면_로그인페이지_리다이렉트() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("loginMember")).thenReturn(null);

        boolean result = interceptor.preHandle(request, response, null);

        assertThat(result).isFalse();
        verify(response).sendRedirect("/login.do");
    }

    @Test
    void 탈퇴한_회원_세션이면_로그인페이지_리다이렉트() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        MemberResponse member = mock(MemberResponse.class);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("loginMember")).thenReturn(member);
        when(member.getDeleteYn()).thenReturn(true);

        boolean result = interceptor.preHandle(request, response, null);

        assertThat(result).isFalse();
        verify(response).sendRedirect("/login.do");
    }
}
