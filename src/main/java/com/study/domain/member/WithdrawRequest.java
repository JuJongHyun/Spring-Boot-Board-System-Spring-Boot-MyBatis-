package com.study.domain.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "회원 탈퇴 요청")
public class WithdrawRequest {

    @Schema(description = "현재 비밀번호 (탈퇴 확인용)")
    private String password;
}
