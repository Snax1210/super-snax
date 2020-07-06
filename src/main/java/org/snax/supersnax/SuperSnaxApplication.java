package org.snax.supersnax;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.snax.supersnax.mapper")
public class SuperSnaxApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(SuperSnaxApplication.class, args);
    }

}
