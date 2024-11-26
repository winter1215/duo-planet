package com.winter.duo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winter.duo.common.BaseResponse;
import com.winter.duo.common.DeleteRequest;
import com.winter.duo.common.ResultUtils;
import com.winter.duo.model.dto.comment.AddComment;
import com.winter.duo.model.dto.comment.CommentPageRequest;
import com.winter.duo.model.entity.Comment;
import com.winter.duo.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author winter
 * @create 2024-03-27 11:34
 */

@RestController
@RequestMapping("/comment")
@Slf4j
public class CommentCtroller {
    @Resource
    private CommentService commentService;

    @PostMapping("/add")
    public BaseResponse<Long> addComment(@RequestBody AddComment addComment) {
        Long commentId = commentService.addComment(addComment);
        return ResultUtils.success(commentId);
    }

    @PostMapping("/remove")
    public BaseResponse<Boolean> removeComment(@RequestBody DeleteRequest deleteRequest) {
        boolean res = commentService.removeComment(deleteRequest.getId());
        return ResultUtils.success(res);
    }

    @PostMapping("/page")
    public BaseResponse<Page<Comment>> pageComment(@RequestBody CommentPageRequest pageRequest) {
        Page<Comment> page = commentService.pageComment(pageRequest.getPostId(), pageRequest.getCurrent(), pageRequest.getPageSize());
        return ResultUtils.success(page);
    }

}
