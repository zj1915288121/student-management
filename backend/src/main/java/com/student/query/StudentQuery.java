package com.student.query;

import lombok.Data;

@Data
public class StudentQuery {

    private String keyword;
    private Long classId;
    private String major;
    private String grade;
    private Integer status;
    private Integer page = 1;
    private Integer size = 10;
}
