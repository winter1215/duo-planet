package com.winter.duo.service;

import com.winter.duo.model.entity.LoginUser;

import javax.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 帖子点赞服务测试

 */
@SpringBootTest
class DiaryThumbServiceTest {

    @Resource
    private PostThumbService postThumbService;

    private static final LoginUser loginUser = new LoginUser();

    @BeforeAll
    static void setUp() {
        loginUser.setUserId(1L);
    }

    @Test
    void doPostThumb() {
        int i = postThumbService.doPostThumb(1L, loginUser);
        Assertions.assertTrue(i >= 0);
    }
}
