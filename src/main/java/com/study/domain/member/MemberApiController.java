package com.study.domain.member;

import com.study.common.dto.ApiResponse;
import com.study.common.exception.BusinessException;
import com.study.common.exception.ErrorCode;
import com.study.domain.member.dto.MemberRequest;
import com.study.domain.member.dto.MemberResponse;
import com.study.domain.member.dto.PasswordChangeRequest;
import com.study.domain.member.dto.ProfileUpdateRequest;
import com.study.domain.member.dto.WithdrawRequest;
import com.study.domain.notification.NotificationUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Tag(name = "Member", description = "회원 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MemberApiController {

    private final MemberService memberService;

    @Value("${file.upload-path}")
    private String uploadPath;

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
        "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of(
        ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    @Operation(summary = "회원가입")
    @PostMapping("/members")
    public ResponseEntity<ApiResponse<Long>> saveMember(@RequestBody MemberRequest params) {
        if (params.getPassword() == null || params.getPassword().length() < 8) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
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

    // ── 마이페이지 접근 비밀번호 확인 ──────────────────────────────────────────

    @Operation(summary = "마이페이지 비밀번호 확인", description = "마이페이지 진입 전 비밀번호를 검증하고 세션에 인증 완료를 기록합니다.")
    @PostMapping("/members/me/verify-password")
    public ResponseEntity<ApiResponse<Void>> verifyMypagePassword(
            @RequestBody java.util.Map<String, String> body,
            HttpSession session) {
        MemberResponse loginMember = getLoginMember(session);
        String password = body.get("password");
        if (password == null || password.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        if (!memberService.verifyPassword(loginMember.getId(), password)) {
            throw new BusinessException(ErrorCode.WRONG_PASSWORD);
        }
        session.setAttribute("mypageVerified", true);
        return ResponseEntity.ok(ApiResponse.ok("인증되었습니다.", null));
    }

    // ── 마이페이지 API ──────────────────────────────────────────

    @Operation(summary = "개인정보 변경", description = "이름, 성별, 생년월일을 변경합니다.")
    @PutMapping("/members/me/profile")
    public ResponseEntity<ApiResponse<MemberResponse>> updateProfile(
            @RequestBody ProfileUpdateRequest params,
            HttpSession session) {
        MemberResponse loginMember = getLoginMember(session);
        memberService.updateProfile(loginMember.getId(), params);
        MemberResponse updated = memberService.findMemberById(loginMember.getId());
        updated.clearPassword();
        session.setAttribute("loginMember", updated);
        return ResponseEntity.ok(ApiResponse.ok("개인정보가 변경되었습니다.", updated));
    }

    @Operation(summary = "프로필 이미지 업로드", description = "프로필 사진을 업로드합니다.")
    @PostMapping(value = "/members/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            HttpSession session) throws IOException {
        MemberResponse loginMember = getLoginMember(session);

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }

        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        }
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(ext)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }

        String savedName = UUID.randomUUID() + ext;

        File dir = new File(uploadPath + "/profile");
        if (!dir.exists()) dir.mkdirs();
        file.transferTo(new File(dir, savedName));

        memberService.updateProfileImage(loginMember.getId(), savedName);

        MemberResponse updated = memberService.findMemberById(loginMember.getId());
        updated.clearPassword();
        session.setAttribute("loginMember", updated);

        return ResponseEntity.ok(ApiResponse.ok("프로필 이미지가 변경되었습니다.", savedName));
    }

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
