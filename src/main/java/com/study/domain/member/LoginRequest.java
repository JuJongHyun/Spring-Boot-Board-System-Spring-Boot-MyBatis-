package com.study.domain.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "로그인 요청")
public class LoginRequest {

    @Schema(description = "로그인 ID", example = "user01")
    private String loginId;

    @Schema(description = "비밀번호", example = "password123")
    private String password;
}
