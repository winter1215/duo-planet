package com.winter.duo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winter.duo.common.BaseResponse;
import com.winter.duo.common.ErrorCode;
import com.winter.duo.common.R;
import com.winter.duo.config.exception.BusinessException;
import com.winter.duo.config.exception.ThrowUtils;
import com.winter.duo.model.dto.diary.DiaryQueryRequest;
import com.winter.duo.model.dto.postfavour.PostFavourAddRequest;
import com.winter.duo.model.dto.postfavour.PostFavourQueryRequest;
import com.winter.duo.model.entity.LoginUser;
import com.winter.duo.model.entity.Diary;
import com.winter.duo.model.vo.DiaryVO;
import com.winter.duo.service.PostFavourService;
import com.winter.duo.service.DiaryService;
import com.winter.duo.service.UserService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 帖子收藏接口
 */
@RestController
@RequestMapping("/post_favour")
@Slf4j
public class PostFavourController {

    @Resource
    private PostFavourService postFavourService;

    @Resource
    private DiaryService diaryService;

    @Resource
    private UserService userService;

    /**
     * 收藏 / 取消收藏
     *
     * @param postFavourAddRequest
     * @param request
     * @return resultNum 收藏变化数
     */
    @PostMapping("/")
    public BaseResponse<Integer> doPostFavour(@RequestBody PostFavourAddRequest postFavourAddRequest,
            HttpServletRequest request) {
        if (postFavourAddRequest == null || postFavourAddRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能操作
        final LoginUser loginUser = userService.getLoginUser();
        long postId = postFavourAddRequest.getPostId();
        int result = postFavourService.doPostFavour(postId, loginUser);
        return R.success(result);
    }

    /**
     * 获取我收藏的帖子列表
     *
     * @param diaryQueryRequest
     * @param request
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<DiaryVO>> listMyFavourPostByPage(@RequestBody DiaryQueryRequest diaryQueryRequest,
                                                              HttpServletRequest request) {
        if (diaryQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUser loginUser = userService.getLoginUser();
        long current = diaryQueryRequest.getCurrent();
        long size = diaryQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Diary> postPage = postFavourService.listFavourPostByPage(new Page<>(current, size),
                diaryService.getQueryWrapper(diaryQueryRequest), loginUser.getUserId());
        return R.success(diaryService.getDiaryVOPage(postPage, request));
    }

    /**
     * 获取用户收藏的帖子列表
     *
     * @param postFavourQueryRequest
     * @param request
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<DiaryVO>> listFavourPostByPage(@RequestBody PostFavourQueryRequest postFavourQueryRequest,
                                                            HttpServletRequest request) {
        if (postFavourQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = postFavourQueryRequest.getCurrent();
        long size = postFavourQueryRequest.getPageSize();
        Long userId = postFavourQueryRequest.getUserId();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20 || userId == null, ErrorCode.PARAMS_ERROR);
        Page<Diary> postPage = postFavourService.listFavourPostByPage(new Page<>(current, size),
                diaryService.getQueryWrapper(postFavourQueryRequest.getDiaryQueryRequest()), userId);
        return R.success(diaryService.getDiaryVOPage(postPage, request));
    }
}
