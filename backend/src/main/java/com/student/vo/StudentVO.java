package com.student.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class StudentVO {

    private Long id;
    private String studentNo;
    private String name;
    private Integer gender;
    private Long classId;
    private String className;
    private String major;
    private String phone;
    private String email;
    private String idCard;
    private String address;
    private LocalDate birthDate;
    private LocalDate enrollmentDate;
    private Integer status;
    private String avatar;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
