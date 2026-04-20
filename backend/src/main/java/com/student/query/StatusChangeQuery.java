package com.student.query;

import lombok.Data;

@Data
public class StatusChangeQuery {

    private String keyword;
    private Long studentId;
    private Integer changeType;
    private Integer status;
    private Integer page = 1;
    private Integer size = 10;
}
