package com.student.vo;

import lombok.Data;

@Data
public class LoginVO {

    private String token;
    private UserInfo userInfo;

    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String realName;
        private Integer role;
        private String roleName;
        private String avatar;
    }
}
