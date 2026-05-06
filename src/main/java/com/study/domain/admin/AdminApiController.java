package com.study.domain.admin;

import com.study.common.dto.ApiResponse;
import com.study.domain.member.MemberRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin", description = "관리자 API")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminApiController {

    private final AdminService adminService;

    @Operation(summary = "회원 권한 변경")
    @PutMapping("/members/{id}/role")
    public ResponseEntity<ApiResponse<Void>> changeRole(
            @PathVariable Long id,
            @RequestBody RoleChangeRequest params) {
        adminService.changeRole(id, params.getRole());
        return ResponseEntity.ok(ApiResponse.ok("권한이 변경되었습니다.", null));
    }

    @Operation(summary = "회원 강제 탈퇴")
    @DeleteMapping("/members/{id}")
    public ResponseEntity<ApiResponse<Void>> forceWithdraw(@PathVariable Long id) {
        adminService.forceWithdraw(id);
        return ResponseEntity.ok(ApiResponse.ok("강제 탈퇴 처리되었습니다.", null));
    }

    @Operation(summary = "게시글 강제 삭제")
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long id) {
        adminService.deletePost(id);
        return ResponseEntity.ok(ApiResponse.ok("게시글이 삭제되었습니다.", null));
    }

    @Getter
    public static class RoleChangeRequest {
        private MemberRole role;
    }
}
