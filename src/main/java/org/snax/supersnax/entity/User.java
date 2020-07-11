package org.snax.supersnax.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User
{
    private Integer id;
    private String userName;
    private String password;
    private String realName;
}
