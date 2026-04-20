package com.student.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("`class`")
public class Classes {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String className;

    private String major;

    private String grade;

    private Long teacherId;

    @TableField(exist = false)
    private String teacherName;

    private Integer studentCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
