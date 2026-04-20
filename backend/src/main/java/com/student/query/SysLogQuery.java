package com.student.query;

import lombok.Data;

@Data
public class SysLogQuery {

    private String keyword;
    private String username;
    private String operation;
    private String dateStart;
    private String dateEnd;
    private Integer page = 1;
    private Integer size = 10;
}
