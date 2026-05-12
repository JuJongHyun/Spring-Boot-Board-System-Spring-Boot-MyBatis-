package com.study.domain.member;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock MemberMapper memberMapper;
    @InjectMocks CustomUserDetailsService service;

    @Test
    void 유효한_사용자_로그인_성공() {
        MemberResponse member = mock(MemberResponse.class);
        when(member.getDeleteYn()).thenReturn(false);
        when(member.getLoginId()).thenReturn("user01");
        when(memberMapper.findByLoginId("user01")).thenReturn(member);

        UserDetails result = service.loadUserByUsername("user01");

        assertThat(result.getUsername()).isEqualTo("user01");
    }

    @Test
    void 존재하지_않는_아이디_예외발생() {
        when(memberMapper.findByLoginId("ghost")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("ghost"));
    }

    @Test
    void 탈퇴한_회원_예외발생() {
        MemberResponse member = mock(MemberResponse.class);
        when(member.getDeleteYn()).thenReturn(true);
        when(memberMapper.findByLoginId("deleted")).thenReturn(member);

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("deleted"));
    }

    @Test
    void 예외메시지에_loginId_미포함_계정열거방지() {
        when(memberMapper.findByLoginId("secret_user")).thenReturn(null);

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("secret_user"));

        assertThat(ex.getMessage()).doesNotContain("secret_user");
    }
}
