package com.student.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "没有权限访问该资源"),
    NOT_FOUND(404, "请求资源不存在"),
    BAD_REQUEST(400, "请求参数错误"),
    TOKEN_EXPIRED(401, "Token已过期，请重新登录"),
    TOKEN_INVALID(401, "Token无效，请重新登录");

    private final Integer code;
    private final String message;
}
