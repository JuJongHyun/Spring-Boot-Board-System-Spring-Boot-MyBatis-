package com.study.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.domain.member.CustomUserDetails;
import com.study.domain.member.MemberResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 비활성화 (AJAX 기반 API + 세션 인증 구조에 적합)
            .csrf(csrf -> csrf.disable())

            // URL 접근 권한
            .authorizeHttpRequests(auth -> auth
                // 정적 리소스
                .requestMatchers("/css/**", "/js/**", "/images/**", "/profile-images/**").permitAll()
                // Swagger
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // 인증 없이 접근 가능한 페이지/API
                .requestMatchers("/login.do", "/login", "/logout").permitAll()
                .requestMatchers("/api/v1/members", "/api/v1/members/check-id").permitAll()
                .requestMatchers("/member-count", "/members").permitAll()
                // 관리자 전용
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // 나머지는 로그인 필요
                .anyRequest().authenticated()
            )

            // 폼 로그인 설정
            .formLogin(form -> form
                .loginPage("/login.do")
                .loginProcessingUrl("/login")
                .usernameParameter("loginId")
                .passwordParameter("password")
                .successHandler(successHandler())
                .failureHandler(failureHandler())
                .permitAll()
            )

            // 로그아웃 설정
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login.do")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            // 접근 거부 처리 (인증은 됐지만 권한 없음)
            .exceptionHandling(ex -> ex
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    if (isAjax(request)) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
                        response.getWriter().write(objectMapper.writeValueAsString(
                            Map.of("success", false, "message", "접근 권한이 없습니다.")));
                    } else {
                        response.sendRedirect("/login.do");
                    }
                })
                .authenticationEntryPoint((request, response, authException) -> {
                    if (isAjax(request)) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
                        response.getWriter().write(objectMapper.writeValueAsString(
                            Map.of("success", false, "message", "로그인이 필요합니다.")));
                    } else {
                        response.sendRedirect("/login.do");
                    }
                })
            )

            // 세션 관리 - 동시 로그인 1개로 제한
            .sessionManagement(session -> session
                .maximumSessions(1)
                .expiredUrl("/login.do")
            );

        return http.build();
    }

    // 로그인 성공 핸들러 - AJAX 응답 + session.loginMember 세팅
    private AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            MemberResponse member = userDetails.getMember();
            member.clearPassword();

            HttpSession session = request.getSession();
            session.setAttribute("loginMember", member);
            session.setMaxInactiveInterval(60 * 30);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(
                Map.of("success", true, "message", "로그인 성공")));
        };
    }

    // 로그인 실패 핸들러 - AJAX 응답
    private AuthenticationFailureHandler failureHandler() {
        return (request, response, exception) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(
                Map.of("success", false, "message", "아이디와 비밀번호를 확인해 주세요.")));
        };
    }

    private boolean isAjax(jakarta.servlet.http.HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))
            || (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json"));
    }
}
