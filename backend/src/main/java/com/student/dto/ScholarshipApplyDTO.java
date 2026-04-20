package com.student.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScholarshipApplyDTO {

    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @NotNull(message = "奖学金ID不能为空")
    private Long scholarshipId;

    private String applyReason;
}
