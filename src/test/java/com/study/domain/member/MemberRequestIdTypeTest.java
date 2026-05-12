package com.study.domain.member;

import com.study.domain.member.dto.MemberRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class MemberRequestIdTypeTest {

    @Test
    @DisplayName("[P1-3] MemberRequest.id는 Long(래퍼) 타입이어야 한다 — primitive long은 기본값 0으로 신규/수정 구분 불가")
    void memberRequestId_shouldBeLongWrapper() throws NoSuchFieldException {
        Field idField = MemberRequest.class.getDeclaredField("id");
        assertThat(idField.getType())
                .as("primitive long은 기본값이 0이므로 null 체크 불가 — Long 래퍼 타입이어야 함")
                .isEqualTo(Long.class);
    }

    @Test
    @DisplayName("[P1-3] 새 MemberRequest 인스턴스의 id 기본값은 null이어야 한다")
    void memberRequestId_defaultShouldBeNull() {
        MemberRequest params = new MemberRequest();
        assertThat(params.getId())
                .as("신규 객체의 id는 null이어야 신규/수정을 구분할 수 있음")
                .isNull();
    }
}
