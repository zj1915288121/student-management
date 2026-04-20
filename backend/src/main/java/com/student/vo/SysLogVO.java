package com.student.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysLogVO {

    private Long id;
    private Long userId;
    private String username;
    private String operation;
    private String method;
    private String params;
    private String ip;
    private Long time;
    private LocalDateTime createTime;
}
