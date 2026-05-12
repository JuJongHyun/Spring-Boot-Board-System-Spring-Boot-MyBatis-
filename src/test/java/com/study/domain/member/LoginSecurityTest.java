package com.study.domain.member;

import com.study.config.SecurityConfig;
import com.study.domain.admin.AdminService;
import com.study.domain.comment.CommentService;
import com.study.domain.file.FileService;
import com.study.domain.notification.NotificationService;
import com.study.domain.notification.SseEmitterService;
import com.study.domain.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(excludeAutoConfiguration = UserDetailsServiceAutoConfiguration.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "file.upload-path=/tmp/test-uploads")
class LoginSecurityTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean CustomUserDetailsService userDetailsService;
    @MockitoBean AdminService adminService;
    @MockitoBean PostService postService;
    @MockitoBean CommentService commentService;
    @MockitoBean MemberService memberService;
    @MockitoBean FileService fileService;
    @MockitoBean NotificationService notificationService;
    @MockitoBean SseEmitterService sseEmitterService;

    private CustomUserDetails validUser;

    @BeforeEach
    void setUp() {
        String encoded = new BCryptPasswordEncoder().encode("password123");

        MemberResponse member = mock(MemberResponse.class);
        when(member.getId()).thenReturn(1L);
        when(member.getLoginId()).thenReturn("user01");
        when(member.getPassword()).thenReturn(encoded);
        when(member.getName()).thenReturn("테스터");
        when(member.getRole()).thenReturn(MemberRole.USER);
        when(member.getDeleteYn()).thenReturn(false);

        validUser = new CustomUserDetails(member);
        when(userDetailsService.loadUserByUsername("user01")).thenReturn(validUser);
    }

    // ── 접근 제어 ─────────────────────────────────────────────────────────────

    @Test
    void 로그인_페이지_비인증_접근_허용() throws Exception {
        mockMvc.perform(get("/login.do"))
                .andExpect(status().isOk());
    }

    @Test
    void 보호된_페이지_비인증_접근시_로그인페이지로_리다이렉트() throws Exception {
        mockMvc.perform(get("/post/list.do"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login.do"));
    }

    @Test
    void 보호된_페이지_AJAX_비인증_접근시_401_JSON반환() throws Exception {
        mockMvc.perform(get("/post/list.do")
                        .header("X-Requested-With", "XMLHttpRequest"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "user01", roles = "USER")
    void 로그인_후_보호된_페이지_접근_허용() throws Exception {
        mockMvc.perform(get("/post/list.do"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void 일반_사용자_관리자_페이지_접근_거부() throws Exception {
        mockMvc.perform(get("/admin/members"))
                .andExpect(status().isForbidden());
    }

    // ── 로그인 폼 인증 ─────────────────────────────────────────────────────────

    @Test
    void 로그인_성공시_인증_처리됨() throws Exception {
        mockMvc.perform(formLogin("/login")
                        .user("loginId", "user01")
                        .password("password", "password123"))
                .andExpect(authenticated().withUsername("user01"));
    }

    @Test
    void 잘못된_비밀번호_로그인_실패() throws Exception {
        when(userDetailsService.loadUserByUsername("user01"))
                .thenThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("Invalid credentials"));

        mockMvc.perform(formLogin("/login")
                        .user("loginId", "user01")
                        .password("password", "wrong"))
                .andExpect(unauthenticated());
    }

    @Test
    void 로그인_성공시_200_JSON_success_true() throws Exception {
        mockMvc.perform(formLogin("/login")
                        .user("loginId", "user01")
                        .password("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void 로그인_실패시_401_JSON_success_false() throws Exception {
        when(userDetailsService.loadUserByUsername("wrong"))
                .thenThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("Invalid credentials"));

        mockMvc.perform(formLogin("/login")
                        .user("loginId", "wrong")
                        .password("password", "wrong"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void 로그인_성공후_세션에_loginMember_저장() throws Exception {
        mockMvc.perform(formLogin("/login")
                        .user("loginId", "user01")
                        .password("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(request().sessionAttribute("loginMember", validUser.getMember()));
    }

    @Test
    void 로그인_성공후_clearPassword_호출() throws Exception {
        mockMvc.perform(formLogin("/login")
                        .user("loginId", "user01")
                        .password("password", "password123"))
                .andExpect(status().isOk());

        verify(validUser.getMember(), atLeastOnce()).clearPassword();
    }
}
