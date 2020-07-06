package org.snax.supersnax.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.snax.supersnax.entity.User;
import org.snax.supersnax.service.UserService;
import org.snax.supersnax.util.DefaultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController
{
    @Resource
    UserService userService;

    @GetMapping("/get")
    public String getUser(String id){
        try
        {
            User resultUser = userService.get(id);
            return JSONObject.toJSONString(resultUser);
        }
        catch (Exception e)
        {
            log.error("get user error ! reason is {}",e.getMessage());
            return DefaultUtils.ERROR;
        }
    }

    @PostMapping("/insert")
    public String insertUser(String paramString)
    {
        try
        {
            //User user = JSONObject.parseObject(paramString,User.class);
            User user1 =new User(UUID.randomUUID().toString(),"username","password","realname");
            userService.insert(user1);
            return DefaultUtils.SUCCESS;
        }
        catch (Exception e)
        {
            log.error("insert User error reason is {}",e.getMessage());
            return DefaultUtils.ERROR;
        }
    }
}
