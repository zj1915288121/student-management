package com.student.query;

import lombok.Data;

@Data
public class ScholarshipQuery {

    private String keyword;
    private Integer status;
    private Integer page = 1;
    private Integer size = 10;
}
