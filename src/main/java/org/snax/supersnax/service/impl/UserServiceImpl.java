package org.snax.supersnax.service.impl;

import org.snax.supersnax.entity.User;
import org.snax.supersnax.mapper.UserDao;
import org.snax.supersnax.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService
{
    @Resource
    UserDao userDao;
    @Override
    public User get(String id)
    {
        return userDao.get(id);
    }

    @Override
    public int insert(User user)
    {
        return userDao.add(user);
    }
}
