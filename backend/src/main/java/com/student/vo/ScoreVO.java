package com.student.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ScoreVO {

    private Long id;
    private Long studentId;
    private String studentNo;
    private String studentName;
    private Long courseId;
    private String courseName;
    private BigDecimal score;
    private BigDecimal credit;
    private String semester;
    private Integer examType;
    private Boolean passed;
    private LocalDateTime createTime;
}
