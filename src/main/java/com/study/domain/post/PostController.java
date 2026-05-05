package com.study.domain.post;

import com.study.common.dto.MessageDTO;
import com.study.common.dto.SearchDTO;
import com.study.common.paging.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    // 게시글 작성 페이지
    @GetMapping("/write.do")
    public String openPostWrite(@RequestParam(value = "id", required = false) final Long id, Model model) {
        if (id != null) {
            PostResponse post = postService.findPostById(id);
            model.addAttribute("post", post);
        }
        return "post/write";
    }

    // 신규 게시글 생성
    @PostMapping("/save.do")
    public String savePost(final PostRequest params, Model model) {
        postService.savePost(params);
        MessageDTO message = new MessageDTO("게시글 생성이 완료되었습니다.", "/post/list.do", RequestMethod.GET, null);
        return showMessageAndRedirect(message, model);
    }

    // 게시글 리스트 페이지
    @GetMapping("/list.do")
    public String openPostList(@ModelAttribute("params") final SearchDTO params, Model model) {
        PagingResponse<PostResponse> response = postService.findAllPost(params);
        model.addAttribute("response", response);
        return "post/list";
    }

    // 게시글 상세 페이지
    @GetMapping("/view.do")
    public String openPostView(@RequestParam final Long id, Model model) {
        postService.increaseViewCount(id);
        PostResponse post = postService.findPostById(id);
        model.addAttribute("post", post);
        return "post/view";
    }

    // 기존 게시글 수정
    @PostMapping("/update.do")
    public String updatePost(final PostRequest params, final SearchDTO queryParams, Model model) {
        postService.updatePost(params);
        MessageDTO message = new MessageDTO("게시글 수정이 완료되었습니다.", "/post/list.do", RequestMethod.GET, queryParamsToMap(queryParams));
        return showMessageAndRedirect(message, model);
    }

    // 게시글 삭제
    @PostMapping("/delete.do")
    public String deletePost(@RequestParam final long id, final SearchDTO queryParams, Model model) {
        postService.deletePost(id);
        MessageDTO message = new MessageDTO("게시글 삭제가 완료되었습니다.", "/post/list.do", RequestMethod.GET, queryParamsToMap(queryParams));
        return showMessageAndRedirect(message, model);
    }

    private Map<String, Object> queryParamsToMap(final SearchDTO queryParams) {
        Map<String, Object> data = new HashMap<>();
        data.put("page", queryParams.getPage());
        data.put("recordSize", queryParams.getRecordSize());
        data.put("pageSize", queryParams.getPageSize());
        data.put("keyword", queryParams.getKeyword());
        data.put("searchType", queryParams.getSearchType());
        return data;
    }

    private String showMessageAndRedirect(final MessageDTO params, Model model) {
        model.addAttribute("params", params);
        return "common/messageRedirect";
    }
}
