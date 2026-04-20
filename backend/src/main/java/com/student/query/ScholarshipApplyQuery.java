package com.student.query;

import lombok.Data;

@Data
public class ScholarshipApplyQuery {

    private String keyword;
    private Long scholarshipId;
    private Integer status;
    private Integer page = 1;
    private Integer size = 10;
}
