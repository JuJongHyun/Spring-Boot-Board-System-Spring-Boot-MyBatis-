package com.study.domain.comment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThat;

class CommentResponseFieldTest {

    @Test
    @DisplayName("[P1-2] CommentResponse에 'createdDate' 필드가 있어야 한다 (DB: created_date → camelCase)")
    void commentResponse_shouldHaveCreatedDateField() {
        assertThatCode(() -> CommentResponse.class.getDeclaredField("createdDate"))
                .as("MyBatis map-underscore-to-camel-case=true → 'created_date'는 'createdDate'로 매핑됨")
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("[P1-2] CommentResponse에 'createTime' 필드가 없어야 한다 (잘못된 필드명)")
    void commentResponse_shouldNotHaveCreateTimeField() {
        boolean hasCreateTime = false;
        try {
            CommentResponse.class.getDeclaredField("createTime");
            hasCreateTime = true;
        } catch (NoSuchFieldException ignored) {
        }
        assertThat(hasCreateTime)
                .as("'createTime'은 DB 컬럼 'created_date'와 매핑되지 않으므로 존재하면 안 됨")
                .isFalse();
    }
}
