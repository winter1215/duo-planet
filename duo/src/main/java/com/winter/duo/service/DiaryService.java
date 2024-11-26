package com.winter.duo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.winter.duo.model.dto.diary.DiaryQueryRequest;
import com.winter.duo.model.entity.Diary;
import com.winter.duo.model.vo.DiaryVO;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子服务

 */
public interface DiaryService extends IService<Diary> {

    /**
     * 校验
     *
     * @param diary
     * @param add
     */
    void validDiary(Diary diary, boolean add);

    /**
     * 获取查询条件
     *
     * @param diaryQueryRequest
     * @return
     */
    QueryWrapper<Diary> getQueryWrapper(DiaryQueryRequest diaryQueryRequest);


    /**
     * 获取帖子封装
     *
     * @param diary
     * @param request
     * @return
     */
    DiaryVO getDiaryVO(Diary diary, HttpServletRequest request);

    /**
     * 分页获取帖子封装
     *
     * @param postPage
     * @param request
     * @return
     */
    Page<DiaryVO> getDiaryVOPage(Page<Diary> postPage, HttpServletRequest request);
}
