package com.student.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("status_change")
public class StatusChange {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private String studentNo;

    private String studentName;

    private Integer changeType;

    private String reason;

    private String fromInfo;

    private String toInfo;

    private Integer status;

    private LocalDateTime applyTime;

    private LocalDateTime approveTime;

    private String approver;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
