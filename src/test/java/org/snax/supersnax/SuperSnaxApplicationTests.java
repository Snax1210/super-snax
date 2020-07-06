package org.snax.supersnax;

import org.junit.jupiter.api.Test;
import org.snax.supersnax.entity.User;
import org.snax.supersnax.service.UserService;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.UUID;

@SpringBootTest
class SuperSnaxApplicationTests
{

    @Resource
    UserService userService;

    @Test
    void contextLoads()
    {
        User user1 =new User(UUID.randomUUID().toString(),"username","password","realname");
        userService.insert(user1);
    }

}
