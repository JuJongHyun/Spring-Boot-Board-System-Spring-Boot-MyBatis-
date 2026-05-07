package com.study.domain.notification;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;
    private Long receiverId;
    private Long senderId;
    private Long postId;
    private String type;
    private String message;
    private Boolean readYn;
    private LocalDateTime createdDate;
}
