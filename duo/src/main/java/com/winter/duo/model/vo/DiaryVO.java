package com.winter.duo.model.vo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.winter.duo.model.entity.Comment;
import com.winter.duo.model.entity.Diary;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 帖子视图
 */
@Data
public class DiaryVO implements Serializable {

    private final static Gson GSON = new Gson();

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 标签列表
     */
    private List<String> tagList;

    /**
    * 评论列表
    */
    private List<Comment> comments;

    /**
     * 创建人信息
     */
    private UserVO user;

    /**
     * 是否已点赞
     */
    private Boolean hasThumb;

    /**
     * 是否已收藏
     */
    private Boolean hasFavour;

    /**
     * 包装类转对象
     *
     * @param diaryVO
     * @return
     */
    public static Diary voToObj(DiaryVO diaryVO) {
        if (diaryVO == null) {
            return null;
        }
        Diary diary = new Diary();
        BeanUtils.copyProperties(diaryVO, diary);
        List<String> tagList = diaryVO.getTagList();
        if (tagList != null) {
            diary.setTags(GSON.toJson(tagList));
        }
        return diary;
    }

    /**
     * 对象转包装类
     *
     * @param diary
     * @return
     */
    public static DiaryVO objToVo(Diary diary) {
        if (diary == null) {
            return null;
        }
        DiaryVO diaryVO = new DiaryVO();
        BeanUtils.copyProperties(diary, diaryVO);
        diaryVO.setTagList(GSON.fromJson(diary.getTags(), new TypeToken<List<String>>() {
        }.getType()));
        return diaryVO;
    }
}
