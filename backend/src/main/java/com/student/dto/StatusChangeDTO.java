package com.student.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusChangeDTO {

    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @NotNull(message = "异动类型不能为空")
    private Integer changeType;

    private String reason;

    private String fromInfo;

    private String toInfo;
}
