package com.student.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClassesVO {

    private Long id;
    private String className;
    private String major;
    private String grade;
    private Long teacherId;
    private String teacherName;
    private Integer studentCount;
    private LocalDateTime createTime;
}
