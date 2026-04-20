package com.student.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("student")
public class Student {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String studentNo;

    private String name;

    private Integer gender;

    private Long classId;

    private String major;

    private String phone;

    private String email;

    private String idCard;

    private String address;

    private LocalDate birthDate;

    private LocalDate enrollmentDate;

    private Integer status;

    private String avatar;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
