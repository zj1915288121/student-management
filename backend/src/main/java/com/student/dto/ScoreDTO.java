package com.student.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ScoreDTO {

    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    private BigDecimal score;

    private String semester;

    private Integer examType;
}
