package com.study.domain.member;

import com.study.common.dto.ApiResponse;
import com.study.common.exception.BusinessException;
import com.study.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member", description = "회원 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MemberApiController {

    private final MemberService memberService;

    @Operation(summary = "회원가입")
    @PostMapping("/members")
    public ResponseEntity<ApiResponse<Long>> saveMember(@RequestBody MemberRequest params) {
        if (memberService.countMemberByLoginId(params.getLoginId()) > 0) {
            throw new BusinessException(ErrorCode.DUPLICATE_LOGIN_ID);
        }
        Long id = memberService.saveMember(params);
        return ResponseEntity.status(201).body(ApiResponse.created(id));
    }

    @Operation(summary = "회원 조회")
    @GetMapping("/members/{loginId}")
    public ResponseEntity<ApiResponse<MemberResponse>> findMemberByLoginId(
            @Parameter(description = "로그인 ID") @PathVariable String loginId) {
        MemberResponse member = memberService.findMemberByLoginId(loginId);
        if (member == null || Boolean.TRUE.equals(member.getDeleteYn())) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        member.clearPassword();
        return ResponseEntity.ok(ApiResponse.ok(member));
    }

    @Operation(summary = "아이디 중복 체크")
    @GetMapping("/members/check-id")
    public ResponseEntity<ApiResponse<Boolean>> checkLoginId(
            @Parameter(description = "로그인 ID") @RequestParam String loginId) {
        boolean available = memberService.countMemberByLoginId(loginId) == 0;
        String message = available ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다.";
        return ResponseEntity.ok(ApiResponse.ok(message, available));
    }

    @Operation(summary = "로그인")
    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<MemberResponse>> login(
            @RequestBody LoginRequest params,
            HttpServletRequest request) {
        MemberResponse member = memberService.login(params.getLoginId(), params.getPassword());
        if (member == null) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }
        HttpSession session = request.getSession();
        session.setAttribute("loginMember", member);
        session.setMaxInactiveInterval(60 * 30);
        return ResponseEntity.ok(ApiResponse.ok("로그인 성공", member));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/auth/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.ok("로그아웃 완료", null));
    }

    // ── 마이페이지 API ──────────────────────────────────────────

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호 검증 후 새 비밀번호로 변경합니다.")
    @PutMapping("/members/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestBody PasswordChangeRequest params,
            HttpSession session) {
        MemberResponse loginMember = getLoginMember(session);
        memberService.changePassword(loginMember.getId(), params);
        return ResponseEntity.ok(ApiResponse.ok("비밀번호가 변경되었습니다.", null));
    }

    @Operation(summary = "알림 설정 변경", description = "댓글/답글 알림 수신 여부를 변경합니다.")
    @PutMapping("/members/me/notifications")
    public ResponseEntity<ApiResponse<Void>> updateNotification(
            @RequestBody NotificationUpdateRequest params,
            HttpSession session) {
        MemberResponse loginMember = getLoginMember(session);
        memberService.updateNotification(loginMember.getId(), params);
        return ResponseEntity.ok(ApiResponse.ok("알림 설정이 저장되었습니다.", null));
    }

    @Operation(summary = "회원탈퇴", description = "비밀번호 확인 후 계정을 삭제합니다.")
    @DeleteMapping("/members/me")
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @RequestBody WithdrawRequest params,
            HttpSession session) {
        MemberResponse loginMember = getLoginMember(session);
        memberService.withdraw(loginMember.getId(), params.getPassword());
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.ok("회원탈퇴가 완료되었습니다.", null));
    }

    private MemberResponse getLoginMember(HttpSession session) {
        MemberResponse member = (MemberResponse) session.getAttribute("loginMember");
        if (member == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return member;
    }
}
