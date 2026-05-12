package com.study.config.security;

import com.study.domain.member.MemberRole;
import com.study.domain.member.dto.MemberResponse;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final MemberResponse member;

    public CustomUserDetails(MemberResponse member) {
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        MemberRole role = (member.getRole() != null) ? member.getRole() : MemberRole.USER;
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override public String getPassword()  { return member.getPassword(); }
    @Override public String getUsername()  { return member.getLoginId(); }
    @Override public boolean isAccountNonExpired()  { return true; }
    @Override public boolean isAccountNonLocked()   { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() {
        return !Boolean.TRUE.equals(member.getDeleteYn());
    }
}
