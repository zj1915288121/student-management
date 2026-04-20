package com.student.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("scholarship")
public class Scholarship {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String level;

    private BigDecimal amount;

    private String description;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
