package com.study.domain.comment;

import com.study.common.exception.BusinessException;
import com.study.common.exception.ErrorCode;
import com.study.common.paging.Pagination;
import com.study.common.paging.PagingResponse;
import com.study.domain.member.dto.MemberResponse;
import com.study.domain.member.MemberService;
import com.study.domain.notification.NotificationService;
import com.study.domain.post.PostResponse;
import com.study.domain.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;
    private final PostService postService;
    private final MemberService memberService;
    private final NotificationService notificationService;

    /**
     * 댓글 저장
     * @param params - 댓글 정보
     * @return Generated PK
     */
    @Transactional
    public Long saveComment(final CommentRequest params) {
        commentMapper.save(params);
        sendNotification(params);
        return params.getId();
    }

    private void sendNotification(CommentRequest params) {
        try {
            PostResponse post = postService.findPostById(params.getPostId());
            if (post == null) return;

            if (params.getParentId() != null) {
                // 대댓글: 원댓글 작성자에게 REPLY 알림
                CommentResponse parent = commentMapper.findById(params.getParentId());
                if (parent == null || parent.getMemberId() == null) return;
                if (parent.getMemberId().equals(params.getMemberId())) return; // 본인 답글 제외
                MemberResponse receiver = memberService.findMemberById(parent.getMemberId());
                if (receiver == null || !Boolean.TRUE.equals(receiver.getReplyNotiYn())) return;
                notificationService.notifyReply(
                    receiver.getId(), params.getMemberId(),
                    post.getId(), post.getTitle(), params.getWriter()
                );
            } else {
                // 댓글: 게시글 작성자에게 COMMENT 알림
                if (post.getMemberId() == null) return;
                if (post.getMemberId().equals(params.getMemberId())) return; // 본인 댓글 제외
                MemberResponse receiver = memberService.findMemberById(post.getMemberId());
                if (receiver == null || !Boolean.TRUE.equals(receiver.getCommentNotiYn())) return;
                notificationService.notify(
                    receiver.getId(), params.getMemberId(),
                    post.getId(), post.getTitle(), params.getWriter()
                );
            }
        } catch (Exception e) {
            log.warn("알림 발송 실패 - postId: {}, error: {}", params.getPostId(), e.getMessage());
        }
    }

    /**
     * 댓글 상세정보 조회
     * @param id - PK
     * @return 댓글 상세정보
     */
    public CommentResponse findCommentById(final Long id) {
        return commentMapper.findById(id);
    }

    /**
     * 댓글 수정
     * @param params - 댓글 정보
     * @return PK
     */
    @Transactional
    public Long updateComment(final CommentRequest params, final Long requesterId, final boolean isAdmin) {
        CommentResponse comment = commentMapper.findById(params.getId());
        if (comment == null) {
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
        }
        if (!isAdmin && !comment.getMemberId().equals(requesterId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        commentMapper.update(params);
        return params.getId();
    }

    /**
     * 댓글 삭제
     * @param id - PK
     * @return PK
     */
    @Transactional
    public Long deleteComment(final Long id, final Long requesterId, final boolean isAdmin) {
        CommentResponse comment = commentMapper.findById(id);
        if (comment == null) {
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
        }
        if (!isAdmin && !comment.getMemberId().equals(requesterId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        commentMapper.deleteById(id);
        return id;
    }

    public int countAllComments() {
        return commentMapper.countAll();
    }

    /**
     * 댓글 리스트 조회
     * @param params - search conditions
     * @return 특정 게시글에 등록된 댓글 리스트
     */
    public PagingResponse<CommentResponse> findAllComment(final CommentSearchDTO params, final Long loginMemberId, final boolean isAdmin) {

        int count = commentMapper.count(params);
        if (count < 1) {
            return new PagingResponse<>(Collections.emptyList(), null);
        }

        Pagination pagination = new Pagination(count, params);
        params.setPagination(pagination);
        List<CommentResponse> list = commentMapper.findAll(params);
        list.forEach(c -> {
            c.setOwner(isAdmin || (loginMemberId != null && loginMemberId.equals(c.getMemberId())));
            List<CommentResponse> replies = commentMapper.findReplies(c.getId());
            replies.forEach(r -> r.setOwner(isAdmin || (loginMemberId != null && loginMemberId.equals(r.getMemberId()))));
            c.setReplies(replies);
        });
        return new PagingResponse<>(list, pagination);
    }
}
