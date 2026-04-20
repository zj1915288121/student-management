package com.student.query;

import lombok.Data;

@Data
public class CourseQuery {

    private String keyword;
    private String semester;
    private Long teacherId;
    private Integer page = 1;
    private Integer size = 10;
}
