package com.student.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysUserVO {

    private Long id;
    private String username;
    private Integer role;
    private String roleName;
    private String realName;
    private String phone;
    private String email;
    private String avatar;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
