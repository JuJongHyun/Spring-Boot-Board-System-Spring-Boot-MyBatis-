package com.study.domain.member;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CustomUserDetailsTest {

    @Test
    void role이_USER면_ROLE_USER_권한반환() {
        MemberResponse member = mock(MemberResponse.class);
        when(member.getRole()).thenReturn(MemberRole.USER);

        CustomUserDetails details = new CustomUserDetails(member);
        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();

        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
    }

    @Test
    void role이_ADMIN이면_ROLE_ADMIN_권한반환() {
        MemberResponse member = mock(MemberResponse.class);
        when(member.getRole()).thenReturn(MemberRole.ADMIN);

        CustomUserDetails details = new CustomUserDetails(member);

        assertThat(details.getAuthorities()).extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void role이_null이면_기본값_ROLE_USER() {
        MemberResponse member = mock(MemberResponse.class);
        when(member.getRole()).thenReturn(null);

        CustomUserDetails details = new CustomUserDetails(member);

        assertThat(details.getAuthorities()).extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
    }

    @Test
    void deleteYn_false이면_isEnabled_true() {
        MemberResponse member = mock(MemberResponse.class);
        when(member.getDeleteYn()).thenReturn(false);

        assertThat(new CustomUserDetails(member).isEnabled()).isTrue();
    }

    @Test
    void deleteYn_true이면_isEnabled_false() {
        MemberResponse member = mock(MemberResponse.class);
        when(member.getDeleteYn()).thenReturn(true);

        assertThat(new CustomUserDetails(member).isEnabled()).isFalse();
    }

    @Test
    void deleteYn_null이면_isEnabled_true() {
        MemberResponse member = mock(MemberResponse.class);
        when(member.getDeleteYn()).thenReturn(null);

        assertThat(new CustomUserDetails(member).isEnabled()).isTrue();
    }

    @Test
    void clearPassword_호출후_빈문자열() {
        MemberResponse member = new MemberResponse();
        CustomUserDetails details = new CustomUserDetails(member);

        member.clearPassword();

        assertThat(details.getPassword()).isEmpty();
    }
}
