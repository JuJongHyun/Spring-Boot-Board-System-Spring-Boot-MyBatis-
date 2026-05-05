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

    @Operation(summary = "회원가입", description = "새 회원을 등록합니다.")
    @PostMapping("/members")
    public ResponseEntity<ApiResponse<Long>> saveMember(@RequestBody MemberRequest params) {
        if (memberService.countMemberByLoginId(params.getLoginId()) > 0) {
            throw new BusinessException(ErrorCode.DUPLICATE_LOGIN_ID);
        }
        Long id = memberService.saveMember(params);
        return ResponseEntity.status(201).body(ApiResponse.created(id));
    }

    @Operation(summary = "회원 조회", description = "로그인 ID로 회원 상세 정보를 조회합니다.")
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

    @Operation(summary = "회원 탈퇴", description = "회원을 소프트 삭제합니다.")
    @DeleteMapping("/members/{id}")
    public ResponseEntity<ApiResponse<Long>> deleteMember(
            @Parameter(description = "회원 ID (PK)") @PathVariable Long id) {
        memberService.deleteMemberById(id);
        return ResponseEntity.ok(ApiResponse.ok("회원 탈퇴가 완료되었습니다.", id));
    }

    @Operation(summary = "아이디 중복 체크", description = "로그인 ID 사용 가능 여부를 확인합니다.")
    @GetMapping("/members/check-id")
    public ResponseEntity<ApiResponse<Boolean>> checkLoginId(
            @Parameter(description = "중복 확인할 로그인 ID") @RequestParam String loginId) {
        boolean available = memberService.countMemberByLoginId(loginId) == 0;
        String message = available ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다.";
        return ResponseEntity.ok(ApiResponse.ok(message, available));
    }

    @Operation(summary = "로그인", description = "아이디/비밀번호로 로그인하고 세션을 생성합니다.")
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

    @Operation(summary = "로그아웃", description = "현재 세션을 종료합니다.")
    @PostMapping("/auth/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.ok("로그아웃 완료", null));
    }
}
