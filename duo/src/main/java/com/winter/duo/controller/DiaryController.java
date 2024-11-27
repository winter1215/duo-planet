package com.winter.duo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.winter.duo.annotation.AuthCheck;
import com.winter.duo.common.BaseResponse;
import com.winter.duo.common.DeleteRequest;
import com.winter.duo.common.ErrorCode;
import com.winter.duo.common.R;
import com.winter.duo.constant.UserConstant;
import com.winter.duo.config.exception.BusinessException;
import com.winter.duo.config.exception.ThrowUtils;
import com.winter.duo.model.dto.diary.DiaryAddRequest;
import com.winter.duo.model.dto.diary.DiaryEditRequest;
import com.winter.duo.model.dto.diary.DiaryQueryRequest;
import com.winter.duo.model.dto.diary.DiaryUpdateRequest;
import com.winter.duo.model.entity.LoginUser;
import com.winter.duo.model.entity.Diary;
import com.winter.duo.model.vo.DiaryVO;
import com.winter.duo.service.DiaryService;
import com.winter.duo.service.UserService;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 帖子接口
 */
@RestController
@RequestMapping("/diary")
@Slf4j
public class DiaryController {

    @Resource
    private DiaryService diaryService;

    @Resource
    private UserService userService;

    private final static Gson GSON = new Gson();

    /**
     * 创建
     *
     * @param diaryAddRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addDiary(@RequestBody DiaryAddRequest diaryAddRequest) {
        if (diaryAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Diary diary = new Diary();
        BeanUtils.copyProperties(diaryAddRequest, diary);
        List<String> tags = diaryAddRequest.getTags();
        if (tags != null) {
            diary.setTags(GSON.toJson(tags));
        }
        diaryService.validDiary(diary, true);
        LoginUser loginUser = userService.getLoginUser();
        diary.setUserId(loginUser.getUserId());
        boolean result = diaryService.save(diary);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newDiaryId = diary.getId();
        return R.success(newDiaryId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteDiary(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUser user = userService.getLoginUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        Diary oldDiary = diaryService.getById(id);
        ThrowUtils.throwIf(oldDiary == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldDiary.getUserId().equals(user.getUserId()) && !userService.isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = diaryService.removeById(id);
        return R.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param diaryUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateDiary(@RequestBody DiaryUpdateRequest diaryUpdateRequest) {
        if (diaryUpdateRequest == null || diaryUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Diary diary = new Diary();
        BeanUtils.copyProperties(diaryUpdateRequest, diary);
        List<String> tags = diaryUpdateRequest.getTags();
        if (tags != null) {
            diary.setTags(GSON.toJson(tags));
        }
        // 参数校验
        diaryService.validDiary(diary, false);
        long id = diaryUpdateRequest.getId();
        // 判断是否存在
        Diary oldDiary = diaryService.getById(id);
        ThrowUtils.throwIf(oldDiary == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = diaryService.updateById(diary);
        return R.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<DiaryVO> getDiaryVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Diary diary = diaryService.getById(id);
        if (diary == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return R.success(diaryService.getDiaryVO(diary, request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param diaryQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<DiaryVO>> listDiaryVOByPage(@RequestBody DiaryQueryRequest diaryQueryRequest,
                                                        HttpServletRequest request) {
        long current = diaryQueryRequest.getCurrent();
        long size = diaryQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Diary> diaryPage = diaryService.page(new Page<>(current, size),
                diaryService.getQueryWrapper(diaryQueryRequest));
        return R.success(diaryService.getDiaryVOPage(diaryPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param diaryQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<DiaryVO>> listMyDiaryVOByPage(@RequestBody DiaryQueryRequest diaryQueryRequest,
                                                          HttpServletRequest request) {
        if (diaryQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUser loginUser = userService.getLoginUser();
        diaryQueryRequest.setUserId(loginUser.getUserId());
        long current = diaryQueryRequest.getCurrent();
        long size = diaryQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Diary> diaryPage = diaryService.page(new Page<>(current, size), diaryService.getQueryWrapper(diaryQueryRequest));
        return R.success(diaryService.getDiaryVOPage(diaryPage, request));
    }

    // endregion

    /**
     * 分页搜索（从 ES 查询，封装类）
     *
     * @param diaryQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/search/page/vo")
    public BaseResponse<Page<DiaryVO>> searchDiaryVOByPage(@RequestBody DiaryQueryRequest diaryQueryRequest,
                                                          HttpServletRequest request) {
        long size = diaryQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Diary> diaryPage = null;
        return R.success(diaryService.getDiaryVOPage(diaryPage, request));
    }

    /**
     * 编辑（用户）
     *
     * @param diaryEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editDiary(@RequestBody DiaryEditRequest diaryEditRequest, HttpServletRequest request) {
        if (diaryEditRequest == null || diaryEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Diary diary = new Diary();
        BeanUtils.copyProperties(diaryEditRequest, diary);
        List<String> tags = diaryEditRequest.getTags();
        if (tags != null) {
            diary.setTags(GSON.toJson(tags));
        }
        // 参数校验
        diaryService.validDiary(diary, false);
        LoginUser loginUser = userService.getLoginUser();
        long id = diaryEditRequest.getId();
        // 判断是否存在
        Diary oldDiary = diaryService.getById(id);
        ThrowUtils.throwIf(oldDiary == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldDiary.getUserId().equals(loginUser.getUserId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = diaryService.updateById(diary);
        return R.success(result);
    }

}
