package com.student.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("course")
public class Course {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String courseName;

    private String courseCode;

    private BigDecimal credit;

    private Long teacherId;

    private String teacherName;

    private String semester;

    private Integer classHours;

    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
