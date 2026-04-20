package com.student.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ScholarshipDTO {

    @NotBlank(message = "奖学金名称不能为空")
    private String name;

    private String level;

    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    private String description;

    private Integer status;
}
