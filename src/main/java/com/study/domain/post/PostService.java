package com.study.domain.post;

import com.study.common.dto.SearchDTO;
import com.study.common.file.FileUtils;
import com.study.common.paging.Pagination;
import com.study.common.paging.PagingResponse;
import com.study.domain.file.FileRequest;
import com.study.domain.file.FileResponse;
import com.study.domain.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostMapper postMapper;
    private final FileService fileService;
    private final FileUtils fileUtils;

    /**
     * 게시글 + 첨부파일 저장 (단일 트랜잭션)
     * @param params - 게시글 정보 (첨부파일 포함)
     * @return Generated PK
     */
    @Transactional
    public Long savePost(final PostRequest params) {
        postMapper.save(params);
        List<FileRequest> files = fileUtils.uploadFiles(params.getFiles());
        fileService.saveFiles(params.getId(), files);
        return params.getId();
    }

    /**
     * 게시글 상세정보 조회
     * @param id - PK
     * @return 게시글 상세정보
     */
    public PostResponse findPostById(final Long id) {
        return postMapper.findById(id);
    }

    /**
     * 게시글 + 첨부파일 수정 (단일 트랜잭션)
     * @param params - 게시글 정보 (신규 파일 및 삭제할 파일 ID 포함)
     * @return PK
     */
    @Transactional
    public Long updatePost(final PostRequest params) {
        postMapper.update(params);
        List<FileRequest> uploadFiles = fileUtils.uploadFiles(params.getFiles());
        fileService.saveFiles(params.getId(), uploadFiles);
        List<FileResponse> deleteFiles = fileService.findAllFileByIds(params.getRemoveFileIds());
        fileUtils.deleteFiles(deleteFiles);
        fileService.deleteAllFileByIds(params.getRemoveFileIds());
        return params.getId();
    }

    /**
     * 게시글 삭제
     * @param id - PK
     * @return PK
     */
    @Transactional
    public Long deletePost(final Long id) {
        postMapper.deleteById(id);
        return id;
    }

    /**
     * 게시글 리스트 조회
     * @param params - search conditions
     * @return list & pagination information
     */
    public PagingResponse<PostResponse> findAllPost(final SearchDTO params) {

        // 조건에 해당하는 데이터가 없는 경우, 응답 데이터에 비어있는 리스트와 null을 담아 반환
        int count = postMapper.count(params);
        if (count < 1) {
            return new PagingResponse<>(Collections.emptyList(), null);
        }

        // Pagination 객체를 생성하여 페이지 정보 계산 후 SearchDTO 타입의 객체의 params에 계산된 페이지 정보 저장
        Pagination pagination = new Pagination(count, params);
        params.setPagination(pagination);

        // 계산된 페이지 정보의 일부(limitStart, recordSize)를 기준으로 리스트 데이터 조회 후 응답 데이터 반환
        List<PostResponse> list = postMapper.findAll(params);
        return new PagingResponse<>(list, pagination);
    }




    /**
     * 게시글 조회 수
     * @return 게시글 조회 수
     */
    public void increaseViewCount(final Long id) {
        postMapper.increaseViewCount(id);
    }

}