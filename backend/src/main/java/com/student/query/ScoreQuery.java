package com.student.query;

import lombok.Data;

@Data
public class ScoreQuery {

    private String keyword;
    private Long studentId;
    private Long courseId;
    private String semester;
    private Integer page = 1;
    private Integer size = 10;
}
