package com.student.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RewardPunishVO {

    private Long id;
    private Long studentId;
    private String studentNo;
    private String studentName;
    private String className;
    private Integer type;
    private String level;
    private String content;
    private String reason;
    private LocalDate recordDate;
    private LocalDateTime createTime;
}
