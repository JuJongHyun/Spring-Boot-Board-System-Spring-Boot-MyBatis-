package com.study.domain.file;

import com.study.common.exception.BusinessException;
import com.study.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Tag(name = "File", description = "파일 API")
@RestController
@RequiredArgsConstructor
public class FileApiController {

    @Value("${file.upload-path}")
    private String uploadPath;

    private final FileService fileService;

    @Operation(summary = "게시글 파일 목록 조회")
    @GetMapping("/posts/{postId}/files")
    public List<FileResponse> findAllFileByPostId(
            @Parameter(description = "게시글 ID") @PathVariable Long postId) {
        return fileService.findAllFileByPostId(postId);
    }

    @Operation(summary = "파일 다운로드")
    @GetMapping("/posts/files/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "파일 ID") @PathVariable Long fileId) throws IOException {

        FileResponse file = fileService.findById(fileId);
        if (file == null) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }
        if (Boolean.TRUE.equals(file.getDeleteYn())) {
            throw new BusinessException(ErrorCode.FILE_DELETED);
        }

        String datePath = file.getCreatedDate().format(DateTimeFormatter.ofPattern("yyMMdd"));
        Path path = Paths.get(uploadPath, datePath, file.getSaveName());
        Resource resource = new FileSystemResource(path);

        if (!resource.exists()) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }

        String encodedName = URLEncoder.encode(file.getOriginalName(), StandardCharsets.UTF_8)
                .replace("+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
