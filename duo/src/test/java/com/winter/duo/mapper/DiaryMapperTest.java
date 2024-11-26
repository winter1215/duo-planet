package com.winter.duo.mapper;

import com.winter.duo.model.entity.Diary;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 帖子数据库操作测试

 */
@SpringBootTest
class DiaryMapperTest {

    @Resource
    private PostMapper postMapper;

    @Test
    void listPostWithDelete() {
        List<Diary> diaryList = postMapper.listPostWithDelete(new Date());
        Assertions.assertNotNull(diaryList);
    }
}