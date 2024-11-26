package com.winter.duo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winter.duo.model.entity.Diary;
import java.util.Date;
import java.util.List;

/**
 * 帖子数据库操作
 */
public interface PostMapper extends BaseMapper<Diary> {

    /**
     * 查询帖子列表（包括已被删除的数据）
     */
    List<Diary> listPostWithDelete(Date minUpdateTime);

}




