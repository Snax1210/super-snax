package org.snax.supersnax.service;

import org.snax.supersnax.entity.User;

public interface UserService
{
    User get (Integer id);
    int insert(User user);
}
