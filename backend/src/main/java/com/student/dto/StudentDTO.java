package com.student.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentDTO {

    @NotBlank(message = "学号不能为空")
    private String studentNo;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotNull(message = "性别不能为空")
    private Integer gender;

    @NotNull(message = "班级不能为空")
    private Long classId;

    private String major;

    private String phone;

    private String email;

    private String idCard;

    private String address;

    private LocalDate birthDate;

    private LocalDate enrollmentDate;

    private Integer status;
}
