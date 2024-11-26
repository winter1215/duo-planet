package com.winter.duo.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winter.duo.common.ErrorCode;
import com.winter.duo.config.exception.BusinessException;
import com.winter.duo.config.exception.ThrowUtils;
import com.winter.duo.config.security.RequestContext;
import com.winter.duo.model.dto.comment.AddComment;
import com.winter.duo.model.entity.Comment;
import com.winter.duo.model.entity.LoginUser;
import com.winter.duo.service.CommentService;
import com.winter.duo.mapper.CommentMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 * @createDate 2024-03-11 17:06:22
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Override
    public Long addComment(AddComment addComment) {
        String content = addComment.getContent();
        Long postId = addComment.getPostId();
        if (StringUtils.isBlank(content) || content.length() > 255) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (postId == null || postId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        LoginUser loginUser = RequestContext.getLoginUser();
        Long userId = loginUser.getUserId();
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPostId(postId);
        comment.setUserId(userId);
        boolean save = this.save(comment);
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
        return comment.getId();
    }

    @Override
    public boolean removeComment(Long id) {
        LoginUser loginUser = RequestContext.getLoginUser();
        Long userId = loginUser.getUserId();
        return this.lambdaUpdate().eq(Comment::getId, id).eq(Comment::getUserId, userId).remove();
    }

    @Override
    public Page<Comment> pageComment(Long postId, Long current, Long size) {
        Page<Comment> page = new Page<>(current, size);
        return this.lambdaQuery().eq(Comment::getPostId, postId).orderByDesc(Comment::getCreateTime).page(page);
    }


}




