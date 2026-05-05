package com.study.domain.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "알림 설정 변경 요청")
public class NotificationUpdateRequest {

    @Schema(description = "댓글 알림 여부")
    private boolean commentNotiYn;

    @Schema(description = "답글 알림 여부")
    private boolean replyNotiYn;
}
