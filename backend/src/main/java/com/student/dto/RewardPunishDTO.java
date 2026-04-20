package com.student.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RewardPunishDTO {

    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @NotNull(message = "类型不能为空")
    private Integer type;

    private String level;

    private String content;

    private String reason;

    private LocalDate recordDate;
}
