package com.student.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("score")
public class Score {

    @TableId(type = IdType.AUTO)
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

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
