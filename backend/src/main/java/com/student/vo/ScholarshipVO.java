package com.student.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ScholarshipVO {

    private Long id;
    private String name;
    private String level;
    private BigDecimal amount;
    private String description;
    private Integer status;
    private LocalDateTime createTime;
}
