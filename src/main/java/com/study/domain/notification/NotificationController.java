package com.study.domain.notification;

import com.study.common.dto.ApiResponse;
import com.study.common.exception.BusinessException;
import com.study.common.exception.ErrorCode;
import com.study.domain.member.MemberResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Tag(name = "Notification", description = "알림 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final SseEmitterService sseEmitterService;

    @Operation(summary = "SSE 알림 구독")
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(HttpSession session) {
        MemberResponse loginMember = getLoginMember(session);
        return sseEmitterService.subscribe(loginMember.getId());
    }

    @Operation(summary = "미읽음 알림 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> findUnread(HttpSession session) {
        MemberResponse loginMember = getLoginMember(session);
        return ResponseEntity.ok(ApiResponse.ok(notificationService.findUnread(loginMember.getId())));
    }

    @Operation(summary = "미읽음 알림 수 조회")
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Integer>> countUnread(HttpSession session) {
        MemberResponse loginMember = getLoginMember(session);
        return ResponseEntity.ok(ApiResponse.ok(notificationService.countUnread(loginMember.getId())));
    }

    @Operation(summary = "알림 읽음 처리")
    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @Operation(summary = "전체 알림 읽음 처리")
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(HttpSession session) {
        MemberResponse loginMember = getLoginMember(session);
        notificationService.markAllAsRead(loginMember.getId());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    private MemberResponse getLoginMember(HttpSession session) {
        MemberResponse member = (MemberResponse) session.getAttribute("loginMember");
        if (member == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return member;
    }
}
