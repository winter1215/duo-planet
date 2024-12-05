package com.winter.duo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.winter.duo.common.ErrorCode;
import com.winter.duo.constant.CommonConstant;
import com.winter.duo.config.exception.BusinessException;
import com.winter.duo.config.exception.ThrowUtils;
import com.winter.duo.mapper.PostFavourMapper;
import com.winter.duo.mapper.PostMapper;
import com.winter.duo.mapper.PostThumbMapper;
import com.winter.duo.model.dto.diary.DiaryQueryRequest;
import com.winter.duo.model.entity.*;
import com.winter.duo.model.vo.DiaryVO;
import com.winter.duo.model.vo.UserVO;
import com.winter.duo.service.CommentService;
import com.winter.duo.service.DiaryService;
import com.winter.duo.service.UserService;
import com.winter.duo.utils.SqlUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 帖子服务实现

 */
@Service
@Slf4j
public class DiaryServiceImpl extends ServiceImpl<PostMapper, Diary> implements DiaryService {
    private final static Gson GSON = new Gson();
    @Resource
    private UserService userService;
    @Resource
    private CommentService commentService;
    @Override
    public void validDiary(Diary diary, boolean add) {
        if (diary == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String content = diary.getContent();
        String tags = diary.getTags();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(content, tags), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param diaryQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Diary> getQueryWrapper(DiaryQueryRequest diaryQueryRequest) {
        QueryWrapper<Diary> queryWrapper = new QueryWrapper<>();
        if (diaryQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = diaryQueryRequest.getSearchText();
        String sortField = diaryQueryRequest.getSortField();
        String sortOrder = diaryQueryRequest.getSortOrder();
        Long id = diaryQueryRequest.getId();
        String title = diaryQueryRequest.getTitle();
        String content = diaryQueryRequest.getContent();
        List<String> tagList = diaryQueryRequest.getTags();
        Long userId = diaryQueryRequest.getUserId();
        Long notId = diaryQueryRequest.getNotId();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("title", searchText).or().like("content", searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        if (CollectionUtils.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    @Override
    public DiaryVO getDiaryVO(Diary diary, HttpServletRequest request) {
        DiaryVO diaryVO = DiaryVO.objToVo(diary);
        long postId = diary.getId();
        // 1. 关联查询用户信息
        Long userId = diary.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        diaryVO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
        LoginUser loginUser = userService.getLoginUser();
//        if (loginUser != null) {
//            // 获取首页评论
//            List<Comment> comments = commentService.pageComment(postId, 1L, 5L).getRecords();
//            diaryVO.setComments(comments);
//        }
        return diaryVO;
    }

    @Override
    public Page<DiaryVO> getDiaryVOPage(Page<Diary> diaryPage, HttpServletRequest request) {
        List<Diary> diaryList = diaryPage.getRecords();
        Page<DiaryVO> postVOPage = new Page<>(diaryPage.getCurrent(), diaryPage.getSize(), diaryPage.getTotal());
        if (CollectionUtils.isEmpty(diaryList)) {
            return postVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = diaryList.stream().map(Diary::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 填充信息
        List<DiaryVO> diaryVOList = diaryList.stream().map(post -> {
            DiaryVO diaryVO = DiaryVO.objToVo(post);
            Long userId = post.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            diaryVO.setUser(userService.getUserVO(user));
            return diaryVO;
        }).collect(Collectors.toList());
        postVOPage.setRecords(diaryVOList);
        return postVOPage;
    }

}




