package com.student.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("reward_punish")
public class RewardPunish {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private String studentNo;

    private String studentName;

    private Integer type;

    private String level;

    private String content;

    private String reason;

    private LocalDate recordDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
