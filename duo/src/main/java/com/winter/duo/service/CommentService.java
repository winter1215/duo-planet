package com.winter.duo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winter.duo.model.dto.comment.AddComment;
import com.winter.duo.model.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @createDate 2024-03-11 17:06:22
*/
public interface CommentService extends IService<Comment> {

    Long addComment(AddComment addComment);

    boolean removeComment(Long id);

    Page<Comment> pageComment(Long postId, Long current, Long size);
}
