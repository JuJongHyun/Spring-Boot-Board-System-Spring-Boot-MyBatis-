package com.study.domain.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberMapper memberMapper;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        MemberResponse member = memberMapper.findByLoginId(loginId);
        if (member == null || Boolean.TRUE.equals(member.getDeleteYn())) {
            throw new UsernameNotFoundException("Invalid credentials");
        }
        return new CustomUserDetails(member);
    }
}
