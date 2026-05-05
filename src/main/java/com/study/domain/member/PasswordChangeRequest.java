package com.study.domain.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "비밀번호 변경 요청")
public class PasswordChangeRequest {

    @Schema(description = "현재 비밀번호")
    private String currentPassword;

    @Schema(description = "새 비밀번호")
    private String newPassword;

    @Schema(description = "새 비밀번호 확인")
    private String newPasswordConfirm;
}
