package com.student.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("scholarship_apply")
public class ScholarshipApply {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private String studentNo;

    private String studentName;

    private Long scholarshipId;

    private String scholarshipName;

    private String applyReason;

    private Integer status;

    private LocalDateTime applyTime;

    private LocalDateTime approveTime;

    private String approver;

    private String remark;
}
