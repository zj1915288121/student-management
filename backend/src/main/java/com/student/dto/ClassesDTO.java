package com.student.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClassesDTO {

    @NotBlank(message = "班级名称不能为空")
    private String className;

    @NotBlank(message = "专业不能为空")
    private String major;

    @NotBlank(message = "年级不能为空")
    private String grade;

    private Long teacherId;

    private String teacherName;
}
