package com.study.domain.file;


import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FileApiController {

    @Value("${file.upload-path}")
    private String uploadPath;

    private final FileService fileService;

    // 파일 리스트 조회
    @GetMapping("/posts/{postId}/files")
    public List<FileResponse> findAllFileByPostId(@PathVariable final Long postId) {
        return fileService.findAllFileByPostId(postId);
    }


    @GetMapping("/posts/files/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable final Long fileId) throws IOException {

        // 1. 파일 조회
        FileResponse file = fileService.findById(fileId);

        // 2. 삭제된 파일 체크
        if (Boolean.TRUE.equals(file.getDeleteYn())) {
            throw new RuntimeException("삭제된 파일입니다.");
        }

        // 3. 업로드 경로
        String datePath = file.getCreatedDate()
                .format(DateTimeFormatter.ofPattern("yyMMdd"));

        // 4. 실제 파일 경로
        Path path = Paths.get(uploadPath, datePath, file.getSaveName());
        Resource resource = new FileSystemResource(path);

        // 5. 파일 존재 체크
        if (!resource.exists()) {
            throw new RuntimeException("파일이 존재하지 않습니다.");
        }

        // 6. 파일명 인코딩
        String encodedName = URLEncoder.encode(file.getOriginalName(), "UTF-8")
                .replace("+", "%20");

        // 7. 응답
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + encodedName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
