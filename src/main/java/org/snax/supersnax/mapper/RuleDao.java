package org.snax.supersnax.mapper;

import org.snax.supersnax.entity.GoRule;
import org.snax.supersnax.entity.JavaRule;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author maoth
 * @date 2021/11/16 15:32
 * @description
 */
@Repository
public interface RuleDao {
    List<GoRule> getGoRules();

    void insertJavaRules(JavaRule javaRule);
}
