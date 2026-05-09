package com.study.domain.post;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("실DB 연결 필요 — 로컬 환경에서 수동 실행")
@SpringBootTest
public class PostServiceTest {

    @Autowired
    PostService postService;

    @Test
    void saveByForeach() {
        for (int i = 1; i <= 1000; i++) {
            PostRequest params = new PostRequest();
            params.setTitle(i + "번 게시글 제목");
            params.setContent(i + "번 게시글 내용");
            params.setWriter("테스터" + i);
            params.setNoticeYn(false);
            postService.savePost(params);
        }
    }

}
