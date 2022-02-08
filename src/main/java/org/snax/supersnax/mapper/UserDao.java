package org.snax.supersnax.mapper;

import org.snax.supersnax.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author snax
 */
@Repository
public interface UserDao
{
    User get(Integer id);

    int add(User user);
    List<String> getMessages(int offset);
}
