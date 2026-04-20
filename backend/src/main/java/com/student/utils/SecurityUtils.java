package com.student.utils;

import com.student.common.BusinessException;
import com.student.common.ResultCode;
import com.student.config.JwtUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Long getCurrentUserId() {
        JwtUserDetails details = getJwtUserDetails();
        return details != null ? details.getUserId() : null;
    }

    public static Integer getCurrentRole() {
        JwtUserDetails details = getJwtUserDetails();
        return details != null ? details.getRole() : null;
    }

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }

    public static boolean isAdmin() {
        Integer role = getCurrentRole();
        return role != null && role == 1;
    }

    public static boolean isTeacher() {
        Integer role = getCurrentRole();
        return role != null && role == 2;
    }

    public static boolean isStudent() {
        Integer role = getCurrentRole();
        return role != null && role == 3;
    }

    public static void checkAdmin() {
        if (!isAdmin()) {
            throwForbidden();
        }
    }

    public static void checkAdminOrTeacher() {
        Integer role = getCurrentRole();
        if (role == null || (role != 1 && role != 2)) {
            throwForbidden();
        }
    }

    public static void checkSelfOrAdmin(Long userId) {
        if (isAdmin()) return;
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null || !currentUserId.equals(userId)) {
            throwForbidden();
        }
    }

    public static void throwForbidden() {
        throw new BusinessException(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage());
    }

    private static JwtUserDetails getJwtUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof JwtUserDetails) {
            return (JwtUserDetails) authentication.getDetails();
        }
        return null;
    }
}
