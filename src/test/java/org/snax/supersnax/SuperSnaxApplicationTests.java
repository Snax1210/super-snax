package org.snax.supersnax;

import org.junit.jupiter.api.Test;
import org.snax.supersnax.entity.User;
import org.snax.supersnax.service.UserService;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class SuperSnaxApplicationTests
{

    @Resource
    UserService userService;

    @Test
    void contextLoads()
    {
        User user1 = User.builder().userName("userName").password("password").realName("realName").build();
        userService.insert(user1);
    }

}
