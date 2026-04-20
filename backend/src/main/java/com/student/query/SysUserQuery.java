package com.student.query;

import lombok.Data;

@Data
public class SysUserQuery {

    private String keyword;
    private Integer role;
    private Integer status;
    private Integer page = 1;
    private Integer size = 10;
}
