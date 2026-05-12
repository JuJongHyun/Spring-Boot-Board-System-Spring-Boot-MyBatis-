package com.study.domain.member.dto;

import com.study.domain.member.Gender;
import com.study.domain.member.MemberRole;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class MemberResponse {

    private Long id;                       // 회원 번호 (PK)
    private String loginId;                // 로그인 ID
    private String password;               // 비밀번호
    private String name;                   // 이름
    private Gender gender;                 // 성별
    private LocalDate birthday;            // 생년월일
    private Boolean deleteYn;              // 삭제 여부
    private LocalDateTime createdDate;     // 생성일시
    private LocalDateTime modifiedDate;    // 최종 수정일시
    private Boolean commentNotiYn;         // 댓글 알림 여부
    private Boolean replyNotiYn;           // 답글 알림 여부
    private String profileImage;           // 프로필 이미지 파일명
    private MemberRole role;               // 권한 (USER / ADMIN)

    public void clearPassword() {
        this.password = "";
    }
}
