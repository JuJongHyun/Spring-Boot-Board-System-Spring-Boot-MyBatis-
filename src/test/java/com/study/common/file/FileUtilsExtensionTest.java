package com.study.common.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class FileUtilsExtensionTest {

    private String invokeGenerateSaveFilename(String filename) throws Exception {
        FileUtils fileUtils = new FileUtils();
        Method method = FileUtils.class.getDeclaredMethod("generateSaveFilename", String.class);
        method.setAccessible(true);
        return (String) method.invoke(fileUtils, filename);
    }

    @Test
    @DisplayName("[P0-3] 확장자 없는 파일명은 'null' 문자열을 포함하면 안 된다")
    void generateSaveFilename_withNoExtension_shouldNotContainLiteralNull() throws Exception {
        String result = invokeGenerateSaveFilename("noextensionfile");
        assertThat(result)
                .as("확장자 없는 파일명에서 'null' 문자열 포함 금지")
                .doesNotContain("null");
    }

    @Test
    @DisplayName("[P0-3] 정상 확장자는 저장 파일명에 그대로 반영되어야 한다")
    void generateSaveFilename_withValidExtension_shouldPreserveExtension() throws Exception {
        String result = invokeGenerateSaveFilename("document.pdf");
        assertThat(result).endsWith(".pdf");
    }

    @Test
    @DisplayName("[P0-3] null 파일명도 NPE 없이 'null' 문자열 없이 처리되어야 한다")
    void generateSaveFilename_withNullFilename_shouldNotProduceLiteralNull() throws Exception {
        String result = invokeGenerateSaveFilename(null);
        assertThat(result)
                .as("null 파일명에서 'null' 문자열 포함 금지")
                .doesNotContain("null");
    }
}
