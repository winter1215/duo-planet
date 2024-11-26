package com.winter.duo.service;

import com.winter.duo.model.dto.diary.DiaryQueryRequest;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 帖子服务测试

 */
@SpringBootTest
class DiaryServiceTest {

    @Resource
    private DiaryService diaryService;

    @Test
    void searchFromEs() {
        DiaryQueryRequest diaryQueryRequest = new DiaryQueryRequest();
        diaryQueryRequest.setUserId(1L);
//        Page<Post> postPage = postService.searchFromEs(postQueryRequest);
//        Assertions.assertNotNull(postPage);
    }

}