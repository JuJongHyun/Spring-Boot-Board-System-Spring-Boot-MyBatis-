package com.study.domain.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationMapper notificationMapper;
    private final SseEmitterService sseEmitterService;

    // REQUIRES_NEW: 댓글 트랜잭션과 독립 — 알림 실패가 댓글 저장을 롤백하지 않음
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notify(Long receiverId, Long senderId, Long postId, String postTitle, String senderName) {
        String message = "'" + postTitle + "' 게시글에 " + senderName + "님이 댓글을 달았습니다.";
        NotificationResponse notification = NotificationResponse.builder()
                .receiverId(receiverId)
                .senderId(senderId)
                .postId(postId)
                .type("COMMENT")
                .message(message)
                .build();
        notificationMapper.save(notification);
        sseEmitterService.send(receiverId, notification);
    }

    public List<NotificationResponse> findUnread(Long memberId) {
        return notificationMapper.findUnreadByReceiverId(memberId);
    }

    public int countUnread(Long memberId) {
        return notificationMapper.countUnread(memberId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationMapper.markAsRead(notificationId);
    }

    @Transactional
    public void markAllAsRead(Long memberId) {
        notificationMapper.markAllAsRead(memberId);
    }
}
