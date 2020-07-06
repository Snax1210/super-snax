package org.snax.supersnax.mapper;

import org.snax.supersnax.entity.User;
import org.springframework.stereotype.Repository;

/**
 * @author snax
 */
@Repository
public interface UserDao
{
    User get(String id);
    int add(User user);
}
