package com.student.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ScholarshipApplyVO {

    private Long id;
    private Long studentId;
    private String studentNo;
    private String studentName;
    private Long scholarshipId;
    private String scholarshipName;
    private BigDecimal amount;
    private String applyReason;
    private Integer status;
    private LocalDateTime applyTime;
    private LocalDateTime approveTime;
    private String approver;
    private String remark;
}
