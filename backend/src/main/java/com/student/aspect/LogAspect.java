package com.student.aspect;

import com.student.annotation.Log;
import com.student.config.JwtUserDetails;
import com.student.entity.SysLog;
import com.student.mapper.SysLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final SysLogMapper sysLogMapper;

    @Around("@annotation(log)")
    public Object around(ProceedingJoinPoint point, Log log) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = point.proceed();
        long time = System.currentTimeMillis() - startTime;

        saveLog(point, log, time);
        return result;
    }

    private void saveLog(ProceedingJoinPoint point, Log log, long time) {
        try {
            SysLog sysLog = new SysLog();
            sysLog.setOperation(log.value());
            MethodSignature signature = (MethodSignature) point.getSignature();
            String className = point.getTarget().getClass().getName();
            String methodName = signature.getName();
            sysLog.setMethod(className + "." + methodName);

            Object[] args = point.getArgs();
            if (args != null && args.length > 0) {
                String params = java.util.Arrays.stream(args)
                        .map(arg -> arg != null ? arg.getClass().getSimpleName() : "null")
                        .reduce((a, b) -> a + "," + b)
                        .orElse("");
                if (params.length() > 200) params = params.substring(0, 200);
                sysLog.setParams(params);
            }

            sysLog.setTime(time);
            sysLog.setCreateTime(LocalDateTime.now());

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                sysLog.setUsername(auth.getName());
                if (auth.getDetails() instanceof JwtUserDetails) {
                    sysLog.setUserId(((JwtUserDetails) auth.getDetails()).getUserId());
                }
            }

            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                sysLog.setIp(getIpAddr(request));
            }

            sysLogMapper.insert(sysLog);
        } catch (Exception ignored) {
        }
    }

    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
