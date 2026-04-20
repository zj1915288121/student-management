package com.student.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StatusChangeVO {

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
    private LocalDateTime createTime;
}
