package com.student.query;

import lombok.Data;

@Data
public class ClassesQuery {

    private String keyword;
    private String major;
    private String grade;
    private Integer page = 1;
    private Integer size = 10;
}
