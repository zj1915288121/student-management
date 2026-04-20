package com.student.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CourseDTO {

    @NotBlank(message = "课程名称不能为空")
    private String courseName;

    private String courseCode;

    @NotNull(message = "学分不能为空")
    private BigDecimal credit;

    private Long teacherId;

    private String teacherName;

    private String semester;

    private Integer classHours;

    private String description;
}
